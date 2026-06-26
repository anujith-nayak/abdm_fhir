package in.gov.abdm.fhir.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object representing patient information received from a hospital system.
 *
 * <p>This DTO acts as the input contract for the FHIR Patient conversion endpoint.
 * It is hospital-system-agnostic and carries only the fields needed to construct
 * a valid HL7 FHIR R4 Patient resource.</p>
 *
 * <p>Validated using Bean Validation (Jakarta Validation 3.x).</p>
 */
public class PatientDTO {

    /**
     * Hospital-assigned patient identifier (e.g. MRN). Must not be blank.
     */
    @NotBlank(message = "Patient ID must not be blank")
    private String patientId;

    /**
     * Full legal name of the patient. Must not be blank.
     */
    @NotBlank(message = "Full name must not be blank")
    private String fullName;

    /**
     * Administrative gender.
     * Accepted values (case-insensitive): male, female, other, unknown.
     */
    @NotBlank(message = "Gender must not be blank")
    @Pattern(
        regexp = "(?i)^(male|female|other|unknown)$",
        message = "Gender must be one of: male, female, other, unknown"
    )
    private String gender;

    /**
     * Date of birth in ISO-8601 format (yyyy-MM-dd). Must not be blank.
     */
    @NotBlank(message = "Date of birth must not be blank")
    @Pattern(
        regexp = "^\\d{4}-\\d{2}-\\d{2}$",
        message = "Date of birth must be in yyyy-MM-dd format"
    )
    private String dateOfBirth;

    /**
     * Contact phone number of the patient. Optional.
     */
    private String phoneNumber;

    /**
     * Physical address of the patient (free-text line). Optional.
     */
    private String address;

    /**
     * Ayushman Bharat Health Account (ABHA) number. Optional.
     */
    private String abhaNumber;

    // -------------------------------------------------------------------------
    // No-arg constructor (required by Jackson)
    // -------------------------------------------------------------------------

    public PatientDTO() {}

    // -------------------------------------------------------------------------
    // All-args constructor
    // -------------------------------------------------------------------------

    public PatientDTO(String patientId, String fullName, String gender,
                      String dateOfBirth, String phoneNumber,
                      String address, String abhaNumber) {
        this.patientId = patientId;
        this.fullName = fullName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.abhaNumber = abhaNumber;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAbhaNumber() { return abhaNumber; }
    public void setAbhaNumber(String abhaNumber) { this.abhaNumber = abhaNumber; }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String patientId;
        private String fullName;
        private String gender;
        private String dateOfBirth;
        private String phoneNumber;
        private String address;
        private String abhaNumber;

        private Builder() {}

        public Builder patientId(String patientId) { this.patientId = patientId; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder gender(String gender) { this.gender = gender; return this; }
        public Builder dateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder abhaNumber(String abhaNumber) { this.abhaNumber = abhaNumber; return this; }

        public PatientDTO build() {
            return new PatientDTO(patientId, fullName, gender, dateOfBirth,
                                  phoneNumber, address, abhaNumber);
        }
    }

    @Override
    public String toString() {
        return "PatientDTO{patientId='" + patientId + "', fullName='" + fullName +
               "', gender='" + gender + "', dateOfBirth='" + dateOfBirth + "'}";
    }
}
