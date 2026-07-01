package in.gov.abdm.fhir.ehr.exception;

/**
 * Thrown when the {@link in.gov.abdm.fhir.ehr.provider.EhrDataProvider} cannot locate
 * a patient record for the requested patient ID.
 */
public class PatientNotFoundException extends RuntimeException {

    private final String patientId;

    public PatientNotFoundException(String patientId) {
        super("Patient not found for ID: " + patientId);
        this.patientId = patientId;
    }

    public PatientNotFoundException(String patientId, Throwable cause) {
        super("Patient not found for ID: " + patientId, cause);
        this.patientId = patientId;
    }

    /** Returns the patient ID that could not be found. */
    public String getPatientId() {
        return patientId;
    }
}
