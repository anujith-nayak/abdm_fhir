package in.gov.abdm.fhir.ehr.mapper;

import in.gov.abdm.fhir.dto.DiagnosticReportDTO;
import in.gov.abdm.fhir.dto.MedicationRequestDTO;
import in.gov.abdm.fhir.dto.ObservationDTO;
import in.gov.abdm.fhir.dto.PatientDTO;
import in.gov.abdm.fhir.dto.PractitionerDTO;
import in.gov.abdm.fhir.ehr.dto.DoctorVisitDTO;
import in.gov.abdm.fhir.ehr.dto.LabResultDTO;
import in.gov.abdm.fhir.ehr.dto.MedicationDTO;
import org.springframework.stereotype.Component;

/**
 * Translates generic EHR DTOs (from the EHR abstraction layer) into the FHIR-layer
 * DTOs already consumed by the existing FHIR mappers.
 *
 * <h2>Responsibility boundary</h2>
 * <ul>
 *   <li>This mapper converts <em>data shape</em> only — EHR DTO → FHIR DTO.</li>
 *   <li>It must <strong>never</strong> create FHIR model objects
 *       ({@code org.hl7.fhir.r4.model.*}). That is the FHIR mapper layer's job.</li>
 *   <li>It must <strong>never</strong> call {@code BundleService} or any FHIR service.</li>
 * </ul>
 *
 * <h2>Design note</h2>
 * <p>Field names between EHR DTOs and FHIR DTOs may differ (e.g. {@code drugName} vs
 * {@code medicineName}). This mapper bridges those naming differences in one place
 * so neither the EHR provider nor the FHIR mapper needs to know about the other's
 * naming conventions.</p>
 */
@Component
public class EhrToFhirDtoMapper {

    // -------------------------------------------------------------------------
    // LabResultDTO → ObservationDTO
    // -------------------------------------------------------------------------

    /**
     * Converts a generic {@link LabResultDTO} from the HMS into an
     * {@link ObservationDTO} ready for FHIR conversion.
     *
     * <p>The {@code testName} field is used as the {@code observationType}.
     * It must match one of the values accepted by
     * {@link in.gov.abdm.fhir.dto.ObservationDTO} validation
     * (blood-pressure, temperature, heart-rate, weight, height, oxygen-saturation).</p>
     *
     * @param labResult the HMS lab result; must not be null
     * @return a populated {@link ObservationDTO}
     */
    public ObservationDTO toObservationDTO(LabResultDTO labResult) {
        return ObservationDTO.builder()
                .observationId(labResult.getResultId())
                .patientId(labResult.getPatientId())
                .observationType(labResult.getTestName())
                .observationValue(labResult.getValue())
                .unit(labResult.getUnit())
                .recordedDate(labResult.getResultDate())
                .build();
    }

    // -------------------------------------------------------------------------
    // MedicationDTO → MedicationRequestDTO
    // -------------------------------------------------------------------------

    /**
     * Converts a generic {@link MedicationDTO} from the HMS into a
     * {@link MedicationRequestDTO} ready for FHIR conversion.
     *
     * @param medication the HMS medication; must not be null
     * @return a populated {@link MedicationRequestDTO}
     */
    public MedicationRequestDTO toMedicationRequestDTO(MedicationDTO medication) {
        return MedicationRequestDTO.builder()
                .prescriptionId(medication.getMedicationId())
                .patientId(medication.getPatientId())
                .medicineName(medication.getDrugName())
                .dosage(medication.getDose())
                .frequency(medication.getFrequency())
                .duration(medication.getDuration())
                .instructions(medication.getInstructions())
                .build();
    }

    // -------------------------------------------------------------------------
    // DoctorVisitDTO → PractitionerDTO
    // -------------------------------------------------------------------------

    /**
     * Extracts the practitioner information from a {@link DoctorVisitDTO} and
     * converts it into a {@link PractitionerDTO} ready for FHIR conversion.
     *
     * @param doctorVisit the HMS doctor visit record; must not be null
     * @return a populated {@link PractitionerDTO}
     */
    public PractitionerDTO toPractitionerDTO(DoctorVisitDTO doctorVisit) {
        return PractitionerDTO.builder()
                .practitionerId(doctorVisit.getDoctorId())
                .fullName(doctorVisit.getDoctorName())
                .specialization(doctorVisit.getSpecialization())
                .department(doctorVisit.getVisit() != null
                        ? doctorVisit.getVisit().getDepartment() : null)
                .build();
    }
}
