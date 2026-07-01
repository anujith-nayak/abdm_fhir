package in.gov.abdm.fhir.ehr.exception;

/**
 * Thrown when an {@link in.gov.abdm.fhir.ehr.provider.EhrDataProvider} implementation
 * exists but has not yet implemented the requested operation.
 *
 * <p>Typically thrown by placeholder provider implementations (e.g.
 * {@code RealHmsProvider}, {@code DatabaseHmsProvider}) where the integration
 * has not been built yet.</p>
 */
public class UnsupportedProviderException extends RuntimeException {

    public UnsupportedProviderException(String operationName, String providerName) {
        super("Operation '" + operationName + "' is not yet implemented by provider: " + providerName);
    }

    public UnsupportedProviderException(String message) {
        super(message);
    }
}
