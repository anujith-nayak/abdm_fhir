package in.gov.abdm.fhir.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object representing practitioner (doctor/clinician) information
 * received from a hospital system.
 *
 * <p>Used as the input contract for {@code POST /fhir/practitioner}.</p>
 */
public class PractitionerDTO {

    /** Hospital-assigned practitioner identifier. Must not be blank. */
    @NotBlank(message = "Practitioner ID must not be blank")
    private String practitionerId;

    /** Full name of the practitioner. Must not be blank. */
    @NotBlank(message = "Full name must not be blank")
    private String fullName;

    /** Clinical specialization (e.g. Cardiology, General Medicine). Optional. */
    private String specialization;

    /** Hospital department the practitioner belongs to. Optional. */
    private String department;

    /** Contact phone number. Optional. */
    private String phoneNumber;

    /** Contact email address. Optional but validated when present. */
    @Email(message = "Email must be a valid email address")
    private String email;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public PractitionerDTO() {}

    public PractitionerDTO(String practitionerId, String fullName, String specialization,
                           String department, String phoneNumber, String email) {
        this.practitionerId = practitionerId;
        this.fullName = fullName;
        this.specialization = specialization;
        this.department = department;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public String getPractitionerId() { return practitionerId; }
    public void setPractitionerId(String practitionerId) { this.practitionerId = practitionerId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String practitionerId;
        private String fullName;
        private String specialization;
        private String department;
        private String phoneNumber;
        private String email;

        private Builder() {}

        public Builder practitionerId(String practitionerId) { this.practitionerId = practitionerId; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder specialization(String specialization) { this.specialization = specialization; return this; }
        public Builder department(String department) { this.department = department; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public Builder email(String email) { this.email = email; return this; }

        public PractitionerDTO build() {
            return new PractitionerDTO(practitionerId, fullName, specialization,
                                       department, phoneNumber, email);
        }
    }

    @Override
    public String toString() {
        return "PractitionerDTO{practitionerId='" + practitionerId + "', fullName='" + fullName + "'}";
    }
}
