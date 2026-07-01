package in.gov.abdm.fhir.ehr.dto;

/**
 * Generic EHR DTO representing a single laboratory or vital-sign result from
 * a Hospital Management System.
 *
 * <p>Maps to the existing {@link in.gov.abdm.fhir.dto.ObservationDTO} via
 * {@link in.gov.abdm.fhir.ehr.mapper.EhrToFhirDtoMapper}.</p>
 */
public class LabResultDTO {

    /** Hospital-assigned result identifier. */
    private String resultId;

    /** Patient this result belongs to. */
    private String patientId;

    /**
     * Test / observation name.
     * Should align with an {@link in.gov.abdm.fhir.dto.ObservationDTO} type key
     * (e.g. "heart-rate", "temperature") for automatic LOINC mapping.
     */
    private String testName;

    /** Result value (numeric string, or "systolic/diastolic" for blood pressure). */
    private String value;

    /** Unit of the result value (e.g. "bpm", "Cel", "mmHg"). */
    private String unit;

    /** Date the result was recorded in yyyy-MM-dd format. */
    private String resultDate;

    /** Reference range provided by the lab (e.g. "60-100"). Optional. */
    private String referenceRange;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public LabResultDTO() {}

    public LabResultDTO(String resultId, String patientId, String testName,
                        String value, String unit, String resultDate,
                        String referenceRange) {
        this.resultId = resultId;
        this.patientId = patientId;
        this.testName = testName;
        this.value = value;
        this.unit = unit;
        this.resultDate = resultDate;
        this.referenceRange = referenceRange;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public String getResultId() { return resultId; }
    public void setResultId(String resultId) { this.resultId = resultId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getResultDate() { return resultDate; }
    public void setResultDate(String resultDate) { this.resultDate = resultDate; }

    public String getReferenceRange() { return referenceRange; }
    public void setReferenceRange(String referenceRange) { this.referenceRange = referenceRange; }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String resultId;
        private String patientId;
        private String testName;
        private String value;
        private String unit;
        private String resultDate;
        private String referenceRange;

        private Builder() {}

        public Builder resultId(String resultId) { this.resultId = resultId; return this; }
        public Builder patientId(String patientId) { this.patientId = patientId; return this; }
        public Builder testName(String testName) { this.testName = testName; return this; }
        public Builder value(String value) { this.value = value; return this; }
        public Builder unit(String unit) { this.unit = unit; return this; }
        public Builder resultDate(String resultDate) { this.resultDate = resultDate; return this; }
        public Builder referenceRange(String referenceRange) { this.referenceRange = referenceRange; return this; }

        public LabResultDTO build() {
            return new LabResultDTO(resultId, patientId, testName, value,
                                    unit, resultDate, referenceRange);
        }
    }
}
