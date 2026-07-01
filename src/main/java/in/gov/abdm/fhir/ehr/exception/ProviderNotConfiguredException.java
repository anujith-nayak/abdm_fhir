package in.gov.abdm.fhir.ehr.exception;

/**
 * Thrown when the application context cannot resolve an {@link in.gov.abdm.fhir.ehr.provider.EhrDataProvider}
 * bean — typically because {@code ehr.provider} is set to an unknown value.
 */
public class ProviderNotConfiguredException extends RuntimeException {

    public ProviderNotConfiguredException(String providerKey) {
        super("No EhrDataProvider is configured for provider key: '" + providerKey +
              "'. Valid values: dummy, real, database, rest");
    }

    public ProviderNotConfiguredException(String message, Throwable cause) {
        super(message, cause);
    }
}
