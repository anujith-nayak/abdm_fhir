package in.gov.abdm.fhir.ehr.service;

import in.gov.abdm.fhir.dto.PatientRecordDTO;

/**
 * Service interface for the EHR abstraction layer.
 *
 * <p>Consumers (e.g. {@link in.gov.abdm.fhir.ehr.controller.EhrController}) depend only on
 * this interface. The concrete implementation ({@link EhrServiceImpl}) is injected by
 * Spring and is the only class that communicates with
 * {@link in.gov.abdm.fhir.ehr.provider.EhrDataProvider}.</p>
 *
 * <p><strong>Dependency rule:</strong> This service must never import FHIR model classes
 * ({@code org.hl7.fhir.*}) or FHIR service classes directly.</p>
 */
public interface EhrService {

    /**
     * Retrieves a complete patient record from the configured HMS provider.
     *
     * @param patientId the hospital-assigned patient identifier; must not be null or blank
     * @return a fully populated {@link PatientRecordDTO} ready for FHIR conversion
     * @throws in.gov.abdm.fhir.ehr.exception.PatientNotFoundException if the patient
     *         cannot be found
     */
    PatientRecordDTO getPatientRecord(String patientId);
}
