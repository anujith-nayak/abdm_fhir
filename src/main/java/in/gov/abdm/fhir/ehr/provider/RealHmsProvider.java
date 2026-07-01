package in.gov.abdm.fhir.ehr.provider;

import in.gov.abdm.fhir.dto.PatientRecordDTO;
import in.gov.abdm.fhir.ehr.exception.UnsupportedProviderException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Placeholder for a Real HMS provider — intended to integrate with the production
 * Hospital Management System once Phase 5 begins.
 *
 * <p>Active when {@code ehr.provider=real}.</p>
 *
 * <h2>Phase 5 TODO</h2>
 * <ul>
 *   <li>Inject an HTTP client (WebClient / RestTemplate) or HMS SDK.</li>
 *   <li>Implement {@link #getPatientRecord} to call the real HMS APIs.</li>
 *   <li>Map the HMS response into a {@link PatientRecordDTO} using
 *       {@link in.gov.abdm.fhir.ehr.mapper.EhrToFhirDtoMapper}.</li>
 *   <li>Handle HMS-specific error codes and translate them into
 *       {@link in.gov.abdm.fhir.ehr.exception.PatientNotFoundException}.</li>
 * </ul>
 *
 * <p>The FHIR conversion layer must never import this class.</p>
 */
@Component
@ConditionalOnProperty(name = "ehr.provider", havingValue = "real")
public class RealHmsProvider implements EhrDataProvider {

    /**
     * @throws UnsupportedProviderException always — not yet implemented.
     */
    @Override
    public PatientRecordDTO getPatientRecord(String patientId) {
        // TODO (Phase 5): Implement Real HMS API integration.
        // Example:
        //   HmsPatientResponse response = hmsClient.fetchPatient(patientId);
        //   return ehrToFhirDtoMapper.toPatientRecordDTO(response);
        throw new UnsupportedProviderException("getPatientRecord", "RealHmsProvider");
    }
}
