package in.gov.abdm.fhir.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object representing a clinical observation received from a hospital system.
 *
 * <p>Used as the input contract for {@code POST /fhir/observation}.</p>
 *
 * <p>Supported observation types (case-insensitive):
 * <ul>
 *   <li>{@code blood-pressure}    — systolic/diastolic reading (mmHg)</li>
 *   <li>{@code temperature}       — body temperature (°C or °F)</li>
 *   <li>{@code heart-rate}        — beats per minute</li>
 *   <li>{@code weight}            — body weight (kg or lb)</li>
 *   <li>{@code height}            — body height (cm or in)</li>
 *   <li>{@code oxygen-saturation} — SpO2 percentage</li>
 * </ul>
 * </p>
 */
public class ObservationDTO {

    /** Hospital-assigned observation identifier. Must not be blank. */
    @NotBlank(message = "Observation ID must not be blank")
    private String observationId;

    /** Reference to the patient this observation belongs to. Must not be blank. */
    @NotBlank(message = "Patient ID must not be blank")
    private String patientId;

    /**
     * Type of observation.
     * Must be one of: blood-pressure, temperature, heart-rate, weight, height, oxygen-saturation.
     */
    @NotBlank(message = "Observation type must not be blank")
    @Pattern(
        regexp = "(?i)^(blood-pressure|temperature|heart-rate|weight|height|oxygen-saturation)$",
        message = "Observation type must be one of: blood-pressure, temperature, heart-rate, weight, height, oxygen-saturation"
    )
    private String observationType;

    /**
     * Observed value as a string.
     * For blood-pressure use format "systolic/diastolic" (e.g. "120/80").
     * For all others, provide a numeric string (e.g. "36.6").
     * Must not be blank.
     */
    @NotBlank(message = "Observation value must not be blank")
    private String observationValue;

    /**
     * Unit of measurement (e.g. mmHg, °C, bpm, kg, cm, %).
     * Must not be blank.
     */
    @NotBlank(message = "Unit must not be blank")
    private String unit;

    /**
     * Date the observation was recorded, in yyyy-MM-dd format. Must not be blank.
     */
    @NotBlank(message = "Recorded date must not be blank")
    @Pattern(
        regexp = "^\\d{4}-\\d{2}-\\d{2}$",
        message = "Recorded date must be in yyyy-MM-dd format"
    )
    private String recordedDate;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ObservationDTO() {}

    public ObservationDTO(String observationId, String patientId, String observationType,
                          String observationValue, String unit, String recordedDate) {
        this.observationId = observationId;
        this.patientId = patientId;
        this.observationType = observationType;
        this.observationValue = observationValue;
        this.unit = unit;
        this.recordedDate = recordedDate;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public String getObservationId() { return observationId; }
    public void setObservationId(String observationId) { this.observationId = observationId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getObservationType() { return observationType; }
    public void setObservationType(String observationType) { this.observationType = observationType; }

    public String getObservationValue() { return observationValue; }
    public void setObservationValue(String observationValue) { this.observationValue = observationValue; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getRecordedDate() { return recordedDate; }
    public void setRecordedDate(String recordedDate) { this.recordedDate = recordedDate; }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String observationId;
        private String patientId;
        private String observationType;
        private String observationValue;
        private String unit;
        private String recordedDate;

        private Builder() {}

        public Builder observationId(String observationId) { this.observationId = observationId; return this; }
        public Builder patientId(String patientId) { this.patientId = patientId; return this; }
        public Builder observationType(String observationType) { this.observationType = observationType; return this; }
        public Builder observationValue(String observationValue) { this.observationValue = observationValue; return this; }
        public Builder unit(String unit) { this.unit = unit; return this; }
        public Builder recordedDate(String recordedDate) { this.recordedDate = recordedDate; return this; }

        public ObservationDTO build() {
            return new ObservationDTO(observationId, patientId, observationType,
                                      observationValue, unit, recordedDate);
        }
    }

    @Override
    public String toString() {
        return "ObservationDTO{observationId='" + observationId + "', patientId='" + patientId +
               "', observationType='" + observationType + "'}";
    }
}
