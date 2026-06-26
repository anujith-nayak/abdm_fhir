package in.gov.abdm.fhir.exception;

/**
 * Thrown when a date string received in a DTO cannot be parsed into a valid calendar date.
 */
public class InvalidDateException extends RuntimeException {

    public InvalidDateException(String message) {
        super(message);
    }

    public InvalidDateException(String message, Throwable cause) {
        super(message, cause);
    }
}
