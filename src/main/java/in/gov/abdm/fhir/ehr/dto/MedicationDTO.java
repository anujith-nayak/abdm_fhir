package in.gov.abdm.fhir.ehr.dto;

/**
 * Generic EHR DTO representing a medication entry from a Hospital Management System.
 *
 * <p>Maps to the existing {@link in.gov.abdm.fhir.dto.MedicationRequestDTO} via
 * {@link in.gov.abdm.fhir.ehr.mapper.EhrToFhirDtoMapper}.</p>
 *
 * <p>This DTO is intentionally separate from the FHIR-layer
 * {@link in.gov.abdm.fhir.dto.MedicationRequestDTO} to keep the EHR abstraction
 * decoupled from FHIR semantics.</p>
 */
public class MedicationDTO {

    /** Hospital-assigned prescription / medication order identifier. */
    private String medicationId;

    /** Patient the medication is prescribed for. */
    private String patientId;

    /** Name of the drug (free text, as stored in the HMS). */
    private String drugName;

    /** Prescribed dose per administration (e.g. "500 mg"). */
    private String dose;

    /** Frequency of administration (e.g. "twice daily", "TDS"). */
    private String frequency;

    /** Duration of the course (e.g. "5 days"). */
    private String duration;

    /** Prescribing instructions for the patient. Optional. */
    private String instructions;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public MedicationDTO() {}

    public MedicationDTO(String medicationId, String patientId, String drugName,
                         String dose, String frequency, String duration, String instructions) {
        this.medicationId = medicationId;
        this.patientId = patientId;
        this.drugName = drugName;
        this.dose = dose;
        this.frequency = frequency;
        this.duration = duration;
        this.instructions = instructions;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public String getMedicationId() { return medicationId; }
    public void setMedicationId(String medicationId) { this.medicationId = medicationId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDrugName() { return drugName; }
    public void setDrugName(String drugName) { this.drugName = drugName; }

    public String getDose() { return dose; }
    public void setDose(String dose) { this.dose = dose; }

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
        private String medicationId;
        private String patientId;
        private String drugName;
        private String dose;
        private String frequency;
        private String duration;
        private String instructions;

        private Builder() {}

        public Builder medicationId(String medicationId) { this.medicationId = medicationId; return this; }
        public Builder patientId(String patientId) { this.patientId = patientId; return this; }
        public Builder drugName(String drugName) { this.drugName = drugName; return this; }
        public Builder dose(String dose) { this.dose = dose; return this; }
        public Builder frequency(String frequency) { this.frequency = frequency; return this; }
        public Builder duration(String duration) { this.duration = duration; return this; }
        public Builder instructions(String instructions) { this.instructions = instructions; return this; }

        public MedicationDTO build() {
            return new MedicationDTO(medicationId, patientId, drugName,
                                     dose, frequency, duration, instructions);
        }
    }
}
