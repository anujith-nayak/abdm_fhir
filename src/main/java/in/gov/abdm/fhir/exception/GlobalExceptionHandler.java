package in.gov.abdm.fhir.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Centralised exception handler for all REST controllers in the ABDM FHIR Service.
 *
 * <p>Maps application exceptions to consistent {@link ErrorResponse} payloads
 * with appropriate HTTP status codes.</p>
 *
 * <ul>
 *   <li>Bean Validation failures → 400 Bad Request (with per-field detail)</li>
 *   <li>Invalid date values       → 400 Bad Request</li>
 *   <li>Illegal argument errors   → 400 Bad Request</li>
 *   <li>All other exceptions      → 500 Internal Server Error</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles Bean Validation failures triggered by {@code @Valid} on controller parameters.
     *
     * @param ex the validation exception containing one or more field errors
     * @return 400 Bad Request with a list of per-field validation messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> ErrorResponse.FieldError.builder()
                        .field(fe.getField())
                        .rejectedValue(fe.getRejectedValue())
                        .message(fe.getDefaultMessage())
                        .build())
                .toList();

        ErrorResponse body = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("One or more fields failed validation. See fieldErrors for details.")
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Handles invalid date strings that pass regex validation but are not real calendar dates
     * (e.g. 2024-02-30).
     *
     * @param ex the exception thrown by the mapper
     * @return 400 Bad Request with the parse error message
     */
    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDateException(InvalidDateException ex) {
        ErrorResponse body = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid Date")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Handles programmatic illegal-argument errors such as unrecognised gender values
     * that slip through validation.
     *
     * @param ex the exception
     * @return 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        ErrorResponse body = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid Argument")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Catch-all handler for any unhandled runtime exceptions.
     *
     * @param ex the exception
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse body = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred. Please contact support.")
                .build();

        return ResponseEntity.internalServerError().body(body);
    }
}
