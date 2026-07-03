package in.gov.abdm.fhir.ehr.provider;

import in.gov.abdm.fhir.dto.PatientRecordDTO;
import in.gov.abdm.fhir.ehr.exception.UnsupportedProviderException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Placeholder for the Real HMS provider — the production integration target.
 *
 * <p>Active when {@code ehr.provider=real} in {@code application.properties}.</p>
 *
 * <h2>How to implement</h2>
 * <p>When the Real HMS becomes available, implement {@link #getPatientRecord} in
 * this class. The only contract is: return a fully populated
 * {@link PatientRecordDTO}. Everything downstream (FHIR conversion, Bundle
 * generation, ABDM integration) stays completely unchanged.</p>
 *
 * <p>Typical implementation options:</p>
 * <ul>
 *   <li><strong>Database access</strong> — add a second {@code DataSource} bean,
 *       create JPA entities for the Real HMS schema under a new
 *       {@code hms.real.entity} package, add read-only repositories, and map
 *       entities into {@link PatientRecordDTO} here.</li>
 *   <li><strong>REST API</strong> — inject a {@code RestTemplate} or
 *       {@code WebClient}, call the Real HMS REST endpoints, and map the
 *       responses into {@link PatientRecordDTO} here.</li>
 *   <li><strong>Any other integration mechanism</strong> — gRPC, messaging,
 *       file-based, etc. The provider is a plain Spring component; use whatever
 *       approach the Real HMS supports.</li>
 * </ul>
 *
 * <h2>Switching to this provider</h2>
 * <p>Change one line in {@code application.properties}:</p>
 * <pre>
 *   ehr.provider=real
 * </pre>
 * <p>No other code changes are needed anywhere in the FHIR service.</p>
 *
 * <h2>Dependency rules</h2>
 * <ul>
 *   <li>Only this class may import Real HMS entities or Real HMS clients.</li>
 *   <li>No FHIR service, mapper, or bundle class may import this class.</li>
 *   <li>The FHIR conversion layer must remain unaware of whether data originates
 *       from the Dummy HMS or the Real HMS.</li>
 * </ul>
 */
@Component
@ConditionalOnProperty(name = "ehr.provider", havingValue = "real")
public class RealHmsProvider implements EhrDataProvider {

    // TODO: Inject Real HMS repositories or API clients here when implementing.
    // Example:
    //   private final RealHmsPatientRepository patientRepository;
    //   private final RealHmsApiClient apiClient;

    /**
     * @throws UnsupportedProviderException always — not yet implemented.
     */
    @Override
    public PatientRecordDTO getPatientRecord(String patientId) {
        // TODO: Implement Real HMS integration.
        // Steps:
        //   1. Fetch patient data from the Real HMS (via DB, REST, etc.)
        //   2. Map the Real HMS data model into PatientRecordDTO
        //   3. Return the populated PatientRecordDTO
        //
        // The FHIR conversion layer (BundleService, mappers) requires no changes.
        throw new UnsupportedProviderException("getPatientRecord", "RealHmsProvider");
    }
}
