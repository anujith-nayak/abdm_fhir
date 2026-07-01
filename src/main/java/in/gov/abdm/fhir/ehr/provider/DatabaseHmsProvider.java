package in.gov.abdm.fhir.ehr.provider;

import in.gov.abdm.fhir.dto.PatientRecordDTO;
import in.gov.abdm.fhir.ehr.exception.UnsupportedProviderException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Placeholder for a direct database HMS provider — intended to integrate with any
 * HMS that exposes its data through a shared or replicated database.
 *
 * <p>Active when {@code ehr.provider=database}.</p>
 *
 * <h2>Phase 4+ TODO</h2>
 * <ul>
 *   <li>Add JPA / JDBC dependencies and configure the HMS data source.</li>
 *   <li>Create JPA entities / row mappers for the HMS schema.</li>
 *   <li>Implement {@link #getPatientRecord} using the injected repository.</li>
 *   <li>Translate database entities into {@link PatientRecordDTO} using
 *       {@link in.gov.abdm.fhir.ehr.mapper.EhrToFhirDtoMapper}.</li>
 * </ul>
 *
 * <p><strong>Note:</strong> JPA entities must live in the EHR layer, never in the
 * FHIR layer. The FHIR conversion layer must never import this class.</p>
 */
@Component
@ConditionalOnProperty(name = "ehr.provider", havingValue = "database")
public class DatabaseHmsProvider implements EhrDataProvider {

    /**
     * @throws UnsupportedProviderException always — not yet implemented.
     */
    @Override
    public PatientRecordDTO getPatientRecord(String patientId) {
        // TODO (Phase 4+): Implement database HMS integration.
        // Example:
        //   HmsPatientEntity entity = patientRepository.findByPatientId(patientId)
        //       .orElseThrow(() -> new PatientNotFoundException(patientId));
        //   return ehrToFhirDtoMapper.toPatientRecordDTO(entity);
        throw new UnsupportedProviderException("getPatientRecord", "DatabaseHmsProvider");
    }
}
