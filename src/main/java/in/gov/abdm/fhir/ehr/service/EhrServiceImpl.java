package in.gov.abdm.fhir.ehr.service;

import in.gov.abdm.fhir.dto.PatientRecordDTO;
import in.gov.abdm.fhir.ehr.provider.EhrDataProvider;
import org.springframework.stereotype.Service;

/**
 * Concrete implementation of {@link EhrService}.
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Delegate patient record retrieval to the injected {@link EhrDataProvider}.</li>
 *   <li>Never communicate with FHIR resources, FHIR services, or FHIR mappers directly.</li>
 *   <li>Never know whether the provider is Dummy HMS, Real HMS, REST API, or Database.</li>
 * </ul>
 *
 * <p>The active {@link EhrDataProvider} is determined entirely by the
 * {@code ehr.provider} configuration property — no code changes required when
 * switching providers.</p>
 */
@Service
public class EhrServiceImpl implements EhrService {

    private final EhrDataProvider ehrDataProvider;

    /**
     * Constructor injection — Spring resolves the active {@link EhrDataProvider}
     * based on the {@code ehr.provider} property.
     *
     * @param ehrDataProvider the active HMS provider implementation
     */
    public EhrServiceImpl(EhrDataProvider ehrDataProvider) {
        this.ehrDataProvider = ehrDataProvider;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates directly to the provider with no additional transformation.
     * The provider is responsible for returning a valid {@link PatientRecordDTO}.</p>
     */
    @Override
    public PatientRecordDTO getPatientRecord(String patientId) {
        return ehrDataProvider.getPatientRecord(patientId);
    }
}
