package in.gov.abdm.fhir.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object representing a medication prescription received from a hospital system.
 *
 * <p>Used as the input contract for {@code POST /fhir/medication}.</p>
 */
public class MedicationRequestDTO {

    /** Hospital-assigned prescription identifier. Must not be blank. */
    @NotBlank(message = "Prescription ID must not be blank")
    private String prescriptionId;

    /** Reference to the patient for whom the medication is prescribed. Must not be blank. */
    @NotBlank(message = "Patient ID must not be blank")
    private String patientId;

    /** Name of the medication (e.g. Paracetamol 500mg). Must not be blank. */
    @NotBlank(message = "Medicine name must not be blank")
    private String medicineName;

    /** Dosage per administration (e.g. "500 mg", "10 ml"). Must not be blank. */
    @NotBlank(message = "Dosage must not be blank")
    private String dosage;

    /** Frequency of administration (e.g. "twice daily", "every 8 hours"). Must not be blank. */
    @NotBlank(message = "Frequency must not be blank")
    private String frequency;

    /** Duration of the prescription (e.g. "5 days", "2 weeks"). Must not be blank. */
    @NotBlank(message = "Duration must not be blank")
    private String duration;

    /** Additional instructions for the patient (e.g. "Take after food"). Optional. */
    private String instructions;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public MedicationRequestDTO() {}

    public MedicationRequestDTO(String prescriptionId, String patientId, String medicineName,
                                String dosage, String frequency, String duration,
                                String instructions) {
        this.prescriptionId = prescriptionId;
        this.patientId = patientId;
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.frequency = frequency;
        this.duration = duration;
        this.instructions = instructions;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public String getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(String prescriptionId) { this.prescriptionId = prescriptionId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String prescriptionId;
        private String patientId;
        private String medicineName;
        private String dosage;
        private String frequency;
        private String duration;
        private String instructions;

        private Builder() {}

        public Builder prescriptionId(String prescriptionId) { this.prescriptionId = prescriptionId; return this; }
        public Builder patientId(String patientId) { this.patientId = patientId; return this; }
        public Builder medicineName(String medicineName) { this.medicineName = medicineName; return this; }
        public Builder dosage(String dosage) { this.dosage = dosage; return this; }
        public Builder frequency(String frequency) { this.frequency = frequency; return this; }
        public Builder duration(String duration) { this.duration = duration; return this; }
        public Builder instructions(String instructions) { this.instructions = instructions; return this; }

        public MedicationRequestDTO build() {
            return new MedicationRequestDTO(prescriptionId, patientId, medicineName,
                                            dosage, frequency, duration, instructions);
        }
    }

    @Override
    public String toString() {
        return "MedicationRequestDTO{prescriptionId='" + prescriptionId +
               "', patientId='" + patientId + "', medicineName='" + medicineName + "'}";
    }
}
