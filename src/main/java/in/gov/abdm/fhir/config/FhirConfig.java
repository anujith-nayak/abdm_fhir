package in.gov.abdm.fhir.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for HAPI FHIR context and parser beans.
 *
 * <p>{@link FhirContext} is expensive to create and thread-safe — it should be
 * a singleton. The {@link IParser} bean is configured for pretty-printed JSON output.</p>
 */
@Configuration
public class FhirConfig {

    /**
     * Creates and registers the FHIR R4 context as a singleton Spring bean.
     *
     * @return a configured {@link FhirContext} for FHIR R4
     */
    @Bean
    public FhirContext fhirContext() {
        return FhirContext.forR4();
    }

    /**
     * Creates a pretty-printing JSON parser backed by the FHIR R4 context.
     *
     * @param fhirContext the shared FHIR R4 context
     * @return a configured {@link IParser} that serialises FHIR resources to JSON
     */
    @Bean
    public IParser fhirJsonParser(FhirContext fhirContext) {
        IParser parser = fhirContext.newJsonParser();
        parser.setPrettyPrint(true);
        return parser;
    }
}
