package in.gov.abdm.fhir.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * Uniform error response payload returned by the {@link GlobalExceptionHandler}.
 *
 * <p>Fields with null values are omitted from the serialised JSON.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private String timestamp;
    private List<FieldError> fieldErrors;

    private ErrorResponse() {}

    private ErrorResponse(int status, String error, String message,
                          String timestamp, List<FieldError> fieldErrors) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = timestamp;
        this.fieldErrors = fieldErrors;
    }

    // -------------------------------------------------------------------------
    // Getters (Jackson serialisation)
    // -------------------------------------------------------------------------

    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
    public List<FieldError> getFieldErrors() { return fieldErrors; }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private int status;
        private String error;
        private String message;
        private String timestamp = Instant.now().toString();
        private List<FieldError> fieldErrors;

        private Builder() {}

        public Builder status(int status) { this.status = status; return this; }
        public Builder error(String error) { this.error = error; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder timestamp(String timestamp) { this.timestamp = timestamp; return this; }
        public Builder fieldErrors(List<FieldError> fieldErrors) { this.fieldErrors = fieldErrors; return this; }

        public ErrorResponse build() {
            return new ErrorResponse(status, error, message, timestamp, fieldErrors);
        }
    }

    // -------------------------------------------------------------------------
    // FieldError nested class
    // -------------------------------------------------------------------------

    /**
     * Represents a single field-level validation failure.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FieldError {

        private String field;
        private Object rejectedValue;
        private String message;

        private FieldError() {}

        private FieldError(String field, Object rejectedValue, String message) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.message = message;
        }

        public String getField() { return field; }
        public Object getRejectedValue() { return rejectedValue; }
        public String getMessage() { return message; }

        public static FieldErrorBuilder builder() { return new FieldErrorBuilder(); }

        public static final class FieldErrorBuilder {
            private String field;
            private Object rejectedValue;
            private String message;

            private FieldErrorBuilder() {}

            public FieldErrorBuilder field(String field) { this.field = field; return this; }
            public FieldErrorBuilder rejectedValue(Object rejectedValue) { this.rejectedValue = rejectedValue; return this; }
            public FieldErrorBuilder message(String message) { this.message = message; return this; }

            public FieldError build() {
                return new FieldError(field, rejectedValue, message);
            }
        }
    }
}
