package in.gov.abdm.fhir.ehr.config;

import in.gov.abdm.fhir.ehr.exception.ProviderNotConfiguredException;
import in.gov.abdm.fhir.ehr.provider.EhrDataProvider;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Startup configuration for the EHR abstraction layer.
 *
 * <h2>Provider switching</h2>
 * <p>The active {@link EhrDataProvider} is selected by the {@code ehr.provider}
 * property in {@code application.properties}. Valid values:</p>
 * <ul>
 *   <li>{@code dummy}    — {@link in.gov.abdm.fhir.ehr.provider.DummyHmsProvider} (default)
 *       reads from the Dummy HMS ({@code abdm_hms}) MySQL database.</li>
 *   <li>{@code real}     — {@link in.gov.abdm.fhir.ehr.provider.RealHmsProvider}
 *       stub for future Real HMS integration (not yet implemented).</li>
 *   <li>{@code database} — {@link in.gov.abdm.fhir.ehr.provider.DatabaseHmsProvider}
 *       stub for future generic database HMS integration (not yet implemented).</li>
 * </ul>
 *
 * <p>Each provider is conditionally registered via {@code @ConditionalOnProperty}.
 * This class performs a startup validation to log the active provider clearly and
 * throw a {@link ProviderNotConfiguredException} if no provider is active.</p>
 *
 * <h2>Design rule</h2>
 * <p>Changing the active provider requires only updating {@code application.properties}.
 * No changes to the FHIR conversion layer, BundleService, or any mapper are needed.</p>
 */
@Configuration
public class EhrConfig {

    private static final Logger log = LoggerFactory.getLogger(EhrConfig.class);

    private final EhrDataProvider ehrDataProvider;
    private final String providerKey;

    public EhrConfig(EhrDataProvider ehrDataProvider,
                     @Value("${ehr.provider:dummy}") String providerKey) {
        this.ehrDataProvider = ehrDataProvider;
        this.providerKey = providerKey;
    }

    /**
     * Validates at startup that the configured provider is active and logs its name.
     *
     * @throws ProviderNotConfiguredException if no provider matches the configured key
     */
    @PostConstruct
    public void validateProvider() {
        if (ehrDataProvider == null) {
            throw new ProviderNotConfiguredException(providerKey);
        }
        log.info("EHR provider active: [{}] → {}",
                providerKey, ehrDataProvider.getClass().getSimpleName());
    }
}
