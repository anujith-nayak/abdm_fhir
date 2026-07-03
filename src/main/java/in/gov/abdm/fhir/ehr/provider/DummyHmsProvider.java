package in.gov.abdm.fhir.ehr.provider;

import in.gov.abdm.fhir.dto.DiagnosticReportDTO;
import in.gov.abdm.fhir.dto.MedicationRequestDTO;
import in.gov.abdm.fhir.dto.ObservationDTO;
import in.gov.abdm.fhir.dto.PatientDTO;
import in.gov.abdm.fhir.dto.PatientRecordDTO;
import in.gov.abdm.fhir.dto.PractitionerDTO;
import in.gov.abdm.fhir.ehr.exception.PatientNotFoundException;
import in.gov.abdm.fhir.hms.entity.Appointment;
import in.gov.abdm.fhir.hms.entity.Observation;
import in.gov.abdm.fhir.hms.entity.Patient;
import in.gov.abdm.fhir.hms.entity.Prescription;
import in.gov.abdm.fhir.hms.repository.HmsAppointmentRepository;
import in.gov.abdm.fhir.hms.repository.HmsObservationRepository;
import in.gov.abdm.fhir.hms.repository.HmsPatientRepository;
import in.gov.abdm.fhir.hms.repository.HmsPrescriptionRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * HMS provider backed by the real {@code abdm_hms} MySQL database.
 *
 * <p>Active when {@code ehr.provider=dummy} (the default). Despite the class name,
 * this implementation connects to an actual HMS database — it is called "Dummy" only
 * in the sense that it targets the <em>Dummy HMS</em> (the development/test HMS),
 * not a production system.</p>
 *
 * <h2>Data flow</h2>
 * <pre>
 *   HmsPatientRepository
 *         │  Patient entity
 *   HmsAppointmentRepository
 *         │  Appointment entity → first appointment's doctor → PractitionerDTO
 *   HmsPrescriptionRepository
 *         │  Prescription entities → MedicationRequestDTO list
 *   HmsObservationRepository
 *         │  Observation entities → ObservationDTO list (one row → 5 vitals)
 *         ▼
 *   PatientRecordDTO  →  BundleService  →  FHIR Bundle JSON
 * </pre>
 *
 * <h2>Dependency rules</h2>
 * <ul>
 *   <li>This class is the <strong>only</strong> class that imports HMS repositories
 *       or HMS entities.</li>
 *   <li>All entity-to-DTO mapping is done inside this class.</li>
 *   <li>No FHIR model objects ({@code org.hl7.fhir.*}) are created here.</li>
 *   <li>The FHIR conversion layer ({@code BundleService}, mappers, etc.) remains
 *       completely unaware of this class.</li>
 * </ul>
 *
 * <h2>Replacing with a Real HMS provider</h2>
 * <p>When the Real HMS is integrated, create a new {@code RealHmsProvider} that
 * implements {@link EhrDataProvider} and annotate it with
 * {@code @ConditionalOnProperty(name = "ehr.provider", havingValue = "real")}.
 * Change {@code ehr.provider=real} in {@code application.properties}.
 * Nothing else needs to change.</p>
 */
@Component
@ConditionalOnProperty(name = "ehr.provider", havingValue = "dummy", matchIfMissing = true)
public class DummyHmsProvider implements EhrDataProvider {

    private final HmsPatientRepository patientRepository;
    private final HmsAppointmentRepository appointmentRepository;
    private final HmsPrescriptionRepository prescriptionRepository;
    private final HmsObservationRepository observationRepository;

    public DummyHmsProvider(HmsPatientRepository patientRepository,
                            HmsAppointmentRepository appointmentRepository,
                            HmsPrescriptionRepository prescriptionRepository,
                            HmsObservationRepository observationRepository) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.observationRepository = observationRepository;
    }

    /**
     * Retrieves a complete patient record from the HMS database.
     *
     * <p>The {@code patientId} must be the numeric HMS primary key (e.g. "1", "42").
     * A {@link PatientNotFoundException} is thrown if no patient exists for the given ID.</p>
     *
     * @param patientId the numeric HMS patient ID as a string
     * @return a fully populated {@link PatientRecordDTO}
     * @throws PatientNotFoundException   if no patient is found for the given ID
     * @throws IllegalArgumentException   if {@code patientId} is not a valid number
     */
    @Override
    public PatientRecordDTO getPatientRecord(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            throw new PatientNotFoundException(patientId);
        }

        long id;
        try {
            id = Long.parseLong(patientId.trim());
        } catch (NumberFormatException e) {
            throw new PatientNotFoundException(patientId);
        }

        // 1. Load the patient
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(patientId));

        // 2. Load related records
        List<Appointment> appointments = appointmentRepository.findByPatient_PatientId(id);
        List<Prescription> prescriptions = prescriptionRepository.findByPatient_PatientId(id);
        List<Observation> observations = observationRepository.findByPatient_PatientId(id);

        // 3. Map Patient entity → PatientDTO
        PatientDTO patientDTO = mapPatient(patient);

        // 4. Map the first appointment's doctor → PractitionerDTO
        //    If the patient has no appointments yet, use a placeholder practitioner.
        PractitionerDTO practitionerDTO = appointments.isEmpty()
                ? unknownPractitioner()
                : mapDoctor(appointments.get(0));

        // 5. Map Prescription entities → MedicationRequestDTO list
        List<MedicationRequestDTO> medications = prescriptions.stream()
                .map(p -> mapPrescription(p, patientId))
                .toList();

        // 6. Map Observation entities → ObservationDTO list
        //    Each HMS observation row contains 5 vitals; each vital becomes its own DTO.
        List<ObservationDTO> observationDTOs = new ArrayList<>();
        for (Observation obs : observations) {
            observationDTOs.addAll(mapObservation(obs, patientId));
        }

        // 7. Appointments → DiagnosticReportDTO list
        //    Each appointment with a meaningful status maps to a diagnostic report entry.
        List<DiagnosticReportDTO> diagnosticReports = appointments.stream()
                .map(a -> mapAppointmentToReport(a, patientId))
                .toList();

        return PatientRecordDTO.builder()
                .patient(patientDTO)
                .practitioner(practitionerDTO)
                .observations(observationDTOs)
                .medications(medications)
                .diagnosticReports(diagnosticReports)
                .build();
    }

    // -------------------------------------------------------------------------
    // Private mapping helpers — entity → DTO
    // -------------------------------------------------------------------------

    private PatientDTO mapPatient(Patient p) {
        return PatientDTO.builder()
                .patientId(String.valueOf(p.getPatientId()))
                .fullName(p.getFullName())
                .gender(normalizeGender(p.getGender()))
                .dateOfBirth(p.getDateOfBirth() != null ? p.getDateOfBirth().toString() : "")
                .phoneNumber(p.getPhoneNumber())
                .address(p.getAddress())
                .abhaNumber(p.getAbhaNumber())
                .build();
    }

    /**
     * Extracts practitioner info from the doctor linked to an appointment.
     */
    private PractitionerDTO mapDoctor(Appointment appointment) {
        var doctor = appointment.getDoctor();
        return PractitionerDTO.builder()
                .practitionerId(String.valueOf(doctor.getDoctorId()))
                .fullName(doctor.getFullName())
                .specialization(doctor.getSpecialization())
                .department(doctor.getDepartment())
                .phoneNumber(doctor.getPhoneNumber())
                .email(doctor.getEmail())
                .build();
    }

    /**
     * Maps a prescription to a {@link MedicationRequestDTO}.
     *
     * <p>The HMS schema stores {@code medicine_name}, {@code dosage}, and
     * {@code instructions} but not {@code frequency} or {@code duration}.
     * {@code MedicationRequestDTO} validation requires both to be non-blank, so
     * reasonable defaults are applied when the data is not available in the HMS.</p>
     */
    private MedicationRequestDTO mapPrescription(Prescription p, String patientId) {
        return MedicationRequestDTO.builder()
                .prescriptionId(String.valueOf(p.getPrescriptionId()))
                .patientId(patientId)
                .medicineName(p.getMedicineName())
                .dosage(p.getDosage())
                .frequency("as directed")
                .duration("as prescribed")
                .instructions(p.getInstructions() != null ? p.getInstructions() : "")
                .build();
    }

    /**
     * Splits one HMS {@link Observation} row (which stores 5 vitals in columns)
     * into a list of individual {@link ObservationDTO} instances.
     *
     * <p>Each vital maps to an observation type accepted by the FHIR layer:
     * blood-pressure, temperature, heart-rate, weight, height.</p>
     */
    private List<ObservationDTO> mapObservation(Observation obs, String patientId) {
        String date = obs.getRecordedDate() != null ? obs.getRecordedDate().toString() : LocalDate.now().toString();
        String baseId = String.valueOf(obs.getObservationId());

        List<ObservationDTO> result = new ArrayList<>();

        // Blood pressure
        if (obs.getBloodPressure() != null && !obs.getBloodPressure().isBlank()) {
            result.add(ObservationDTO.builder()
                    .observationId(baseId + "-BP")
                    .patientId(patientId)
                    .observationType("blood-pressure")
                    .observationValue(obs.getBloodPressure())
                    .unit("mm[Hg]")
                    .recordedDate(date)
                    .build());
        }

        // Temperature
        if (obs.getTemperature() != null) {
            result.add(ObservationDTO.builder()
                    .observationId(baseId + "-TEMP")
                    .patientId(patientId)
                    .observationType("temperature")
                    .observationValue(obs.getTemperature().toPlainString())
                    .unit("Cel")
                    .recordedDate(date)
                    .build());
        }

        // Heart rate
        if (obs.getHeartRate() != null) {
            result.add(ObservationDTO.builder()
                    .observationId(baseId + "-HR")
                    .patientId(patientId)
                    .observationType("heart-rate")
                    .observationValue(String.valueOf(obs.getHeartRate()))
                    .unit("/min")
                    .recordedDate(date)
                    .build());
        }

        // Weight
        if (obs.getWeight() != null) {
            result.add(ObservationDTO.builder()
                    .observationId(baseId + "-WT")
                    .patientId(patientId)
                    .observationType("weight")
                    .observationValue(obs.getWeight().toPlainString())
                    .unit("kg")
                    .recordedDate(date)
                    .build());
        }

        // Height
        if (obs.getHeight() != null) {
            result.add(ObservationDTO.builder()
                    .observationId(baseId + "-HT")
                    .patientId(patientId)
                    .observationType("height")
                    .observationValue(obs.getHeight().toPlainString())
                    .unit("cm")
                    .recordedDate(date)
                    .build());
        }

        return result;
    }

    /**
     * Maps an HMS {@link Appointment} to a {@link DiagnosticReportDTO}.
     *
     * <p>Appointments represent scheduled visits. The appointment status is used as
     * the report status (normalised to a valid FHIR DiagnosticReport status code).</p>
     */
    private DiagnosticReportDTO mapAppointmentToReport(Appointment a, String patientId) {
        String date = a.getAppointmentDate() != null ? a.getAppointmentDate().toString() : LocalDate.now().toString();
        String reportStatus = mapAppointmentStatusToFhir(a.getStatus());
        String doctorName = a.getDoctor() != null ? a.getDoctor().getFullName() : "Unknown";

        return DiagnosticReportDTO.builder()
                .reportId("APT-" + a.getAppointmentId())
                .patientId(patientId)
                .reportName("Appointment with " + doctorName)
                .reportStatus(reportStatus)
                .conclusion("Status: " + a.getStatus())
                .reportDate(date)
                .build();
    }

    // -------------------------------------------------------------------------
    // Value normalization helpers
    // -------------------------------------------------------------------------

    /**
     * Normalizes the HMS gender string to a valid FHIR gender value.
     * FHIR accepts: male, female, other, unknown.
     */
    private String normalizeGender(String gender) {
        if (gender == null) return "unknown";
        return switch (gender.trim().toLowerCase()) {
            case "male", "m" -> "male";
            case "female", "f" -> "female";
            case "other" -> "other";
            default -> "unknown";
        };
    }

    /**
     * Maps HMS appointment status to a valid FHIR DiagnosticReport status.
     *
     * <p>FHIR accepted values: registered, partial, preliminary, final, amended,
     * corrected, appended, cancelled, entered-in-error, unknown.</p>
     */
    private String mapAppointmentStatusToFhir(String status) {
        if (status == null) return "unknown";
        return switch (status.trim().toLowerCase()) {
            case "completed", "done" -> "final";
            case "scheduled", "confirmed", "pending" -> "registered";
            case "cancelled", "canceled" -> "cancelled";
            case "in-progress", "in progress" -> "preliminary";
            default -> "unknown";
        };
    }

    /**
     * Returns a placeholder practitioner DTO used when a patient has no appointments yet.
     */
    private PractitionerDTO unknownPractitioner() {
        return PractitionerDTO.builder()
                .practitionerId("UNKNOWN")
                .fullName("Unknown Practitioner")
                .specialization("General Medicine")
                .department("General")
                .build();
    }
}
