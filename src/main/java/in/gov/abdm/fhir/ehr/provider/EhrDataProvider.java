package in.gov.abdm.fhir.ehr.provider;

import in.gov.abdm.fhir.dto.PatientRecordDTO;

/**
 * Abstraction interface for retrieving complete patient records from any
 * Hospital Management System.
 *
 * <p><strong>Dependency rule:</strong> The FHIR conversion layer depends only on this
 * interface. It must never know how, or from where, patient data is obtained.
 * Provider implementations are swapped via Spring configuration ({@code ehr.provider}
 * property) with zero changes to the FHIR layer.</p>
 *
 * <p>The {@link PatientRecordDTO} returned is the same DTO already consumed by the
 * existing {@link in.gov.abdm.fhir.bundle.BundleService} — the EHR layer bridges
 * any HMS-specific data model into this contract.</p>
 *
 * <h2>Implementation guide</h2>
 * <ul>
 *   <li>Implement this interface for each HMS integration target.</li>
 *   <li>Register the implementation as a Spring {@code @Component} with a
 *       {@code @ConditionalOnProperty} guard matching the appropriate
 *       {@code ehr.provider} value.</li>
 *   <li>Return a fully populated {@link PatientRecordDTO} or throw
 *       {@link in.gov.abdm.fhir.ehr.exception.PatientNotFoundException}.</li>
 * </ul>
 */
public interface EhrDataProvider {

    /**
     * Retrieves a complete patient record for the given patient ID.
     *
     * @param patientId the hospital-assigned patient identifier; must not be null or blank
     * @return a fully populated {@link PatientRecordDTO} ready for FHIR conversion
     * @throws in.gov.abdm.fhir.ehr.exception.PatientNotFoundException if the patient
     *         cannot be found in the underlying system
     * @throws in.gov.abdm.fhir.ehr.exception.UnsupportedProviderException if this
     *         operation is not yet implemented by the provider
     */
    PatientRecordDTO getPatientRecord(String patientId);
}
