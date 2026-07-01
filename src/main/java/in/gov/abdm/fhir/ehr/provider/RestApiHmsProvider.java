package in.gov.abdm.fhir.ehr.provider;

import in.gov.abdm.fhir.dto.PatientRecordDTO;
import in.gov.abdm.fhir.ehr.exception.UnsupportedProviderException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Placeholder for a generic REST API HMS provider — intended to integrate with any
 * HMS that exposes a REST API but whose data model differs from the primary Real HMS.
 *
 * <p>Active when {@code ehr.provider=rest}.</p>
 *
 * <h2>Phase 4+ TODO</h2>
 * <ul>
 *   <li>Inject Spring {@code WebClient} configured for the target HMS base URL.</li>
 *   <li>Implement {@link #getPatientRecord} to call the REST endpoints.</li>
 *   <li>Translate the REST response into {@link PatientRecordDTO} using
 *       {@link in.gov.abdm.fhir.ehr.mapper.EhrToFhirDtoMapper}.</li>
 * </ul>
 *
 * <p>The FHIR conversion layer must never import this class.</p>
 */
@Component
@ConditionalOnProperty(name = "ehr.provider", havingValue = "rest")
public class RestApiHmsProvider implements EhrDataProvider {

    /**
     * @throws UnsupportedProviderException always — not yet implemented.
     */
    @Override
    public PatientRecordDTO getPatientRecord(String patientId) {
        // TODO (Phase 4+): Implement REST API HMS integration.
        // Example:
        //   String url = hmsBaseUrl + "/api/patients/" + patientId;
        //   HmsRestPatientResponse response = webClient.get().uri(url).retrieve()
        //       .bodyToMono(HmsRestPatientResponse.class).block();
        //   return ehrToFhirDtoMapper.toPatientRecordDTO(response);
        throw new UnsupportedProviderException("getPatientRecord", "RestApiHmsProvider");
    }
}
