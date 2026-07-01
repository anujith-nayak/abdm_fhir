package in.gov.abdm.fhir.ehr.provider;

import in.gov.abdm.fhir.dto.DiagnosticReportDTO;
import in.gov.abdm.fhir.dto.MedicationRequestDTO;
import in.gov.abdm.fhir.dto.ObservationDTO;
import in.gov.abdm.fhir.dto.PatientDTO;
import in.gov.abdm.fhir.dto.PatientRecordDTO;
import in.gov.abdm.fhir.dto.PractitionerDTO;
import in.gov.abdm.fhir.ehr.exception.PatientNotFoundException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Dummy HMS provider — returns hard-coded mock data for development, testing,
 * and demonstration purposes.
 *
 * <p>This implementation is active when {@code ehr.provider=dummy} (the default).</p>
 *
 * <p>The FHIR conversion layer has no knowledge of this class and must never
 * import it directly.</p>
 *
 * <h2>Phase 4 TODO</h2>
 * <p>Replace the static mock data with calls to an actual Dummy HMS REST API
 * or database when Phase 4 begins. Only this class needs to change.</p>
 */
@Component
@ConditionalOnProperty(name = "ehr.provider", havingValue = "dummy", matchIfMissing = true)
public class DummyHmsProvider implements EhrDataProvider {

    /**
     * Returns a realistic mock {@link PatientRecordDTO} for any patient ID that
     * starts with {@code "PAT-"}, or throws {@link PatientNotFoundException} otherwise.
     *
     * @param patientId the hospital-assigned patient identifier
     * @return a mock {@link PatientRecordDTO}
     */
    @Override
    public PatientRecordDTO getPatientRecord(String patientId) {
        // TODO (Phase 4): Replace with actual Dummy HMS REST API call.
        if (patientId == null || !patientId.startsWith("PAT-")) {
            throw new PatientNotFoundException(patientId);
        }

        PatientDTO patient = PatientDTO.builder()
                .patientId(patientId)
                .fullName("Ravi Kumar")
                .gender("male")
                .dateOfBirth("1985-06-15")
                .phoneNumber("9876543210")
                .address("42, MG Road, Bengaluru, Karnataka 560001")
                .abhaNumber("91-1234-5678-9012")
                .build();

        PractitionerDTO practitioner = PractitionerDTO.builder()
                .practitionerId("PRAC-001")
                .fullName("Dr. Ananya Sharma")
                .specialization("General Medicine")
                .department("OPD")
                .phoneNumber("9123456780")
                .email("ananya.sharma@hospital.example.org")
                .build();

        ObservationDTO heartRate = ObservationDTO.builder()
                .observationId("OBS-001")
                .patientId(patientId)
                .observationType("heart-rate")
                .observationValue("78")
                .unit("/min")
                .recordedDate("2024-11-01")
                .build();

        ObservationDTO bloodPressure = ObservationDTO.builder()
                .observationId("OBS-002")
                .patientId(patientId)
                .observationType("blood-pressure")
                .observationValue("120/80")
                .unit("mm[Hg]")
                .recordedDate("2024-11-01")
                .build();

        MedicationRequestDTO medication = MedicationRequestDTO.builder()
                .prescriptionId("RX-001")
                .patientId(patientId)
                .medicineName("Paracetamol 500mg")
                .dosage("500 mg")
                .frequency("twice daily")
                .duration("5 days")
                .instructions("Take after food")
                .build();

        DiagnosticReportDTO report = DiagnosticReportDTO.builder()
                .reportId("RPT-001")
                .patientId(patientId)
                .reportName("Complete Blood Count")
                .reportStatus("final")
                .conclusion("All values within normal range.")
                .reportDate("2024-11-01")
                .build();

        return PatientRecordDTO.builder()
                .patient(patient)
                .practitioner(practitioner)
                .observations(List.of(heartRate, bloodPressure))
                .medications(List.of(medication))
                .diagnosticReports(List.of(report))
                .build();
    }
}
