package in.gov.abdm.fhir.mapper;

import in.gov.abdm.fhir.dto.ObservationDTO;
import in.gov.abdm.fhir.exception.InvalidDateException;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Maps an {@link ObservationDTO} received from a hospital system into a HAPI FHIR R4
 * {@link Observation} resource.
 *
 * <p>Each supported observation type is mapped to its standard
 * <a href="https://loinc.org">LOINC</a> code and UCUM unit:</p>
 *
 * <table border="1">
 *   <tr><th>Type</th><th>LOINC</th><th>Display</th></tr>
 *   <tr><td>blood-pressure</td><td>55284-4</td><td>Blood pressure systolic and diastolic</td></tr>
 *   <tr><td>temperature</td><td>8310-5</td><td>Body temperature</td></tr>
 *   <tr><td>heart-rate</td><td>8867-4</td><td>Heart rate</td></tr>
 *   <tr><td>weight</td><td>29463-7</td><td>Body weight</td></tr>
 *   <tr><td>height</td><td>8302-2</td><td>Body height</td></tr>
 *   <tr><td>oxygen-saturation</td><td>2708-6</td><td>Oxygen saturation</td></tr>
 * </table>
 *
 * <p>Blood pressure values are expected in {@code "systolic/diastolic"} format (e.g. "120/80").
 * All other observation values are encoded as a FHIR {@link Quantity}.</p>
 */
@Component
public class ObservationFhirMapper {

    private static final String LOINC_SYSTEM = "http://loinc.org";
    private static final String HOSPITAL_OBS_SYSTEM = "https://hospital.example.org/observations";
    private static final String PATIENT_REFERENCE_PREFIX = "Patient/";

    /**
     * Converts a validated {@link ObservationDTO} into a FHIR R4 {@link Observation} resource.
     *
     * @param dto the input observation data; must not be null
     * @return a fully populated HAPI FHIR {@link Observation} resource
     * @throws InvalidDateException     if {@code recordedDate} is not a valid calendar date
     * @throws IllegalArgumentException if {@code observationType} is not recognised
     */
    public Observation toFhirObservation(ObservationDTO dto) {
        Observation observation = new Observation();

        // --- Logical ID ---
        observation.setId(dto.getObservationId());

        // --- Identifier ---
        observation.addIdentifier(
            new Identifier()
                .setSystem(HOSPITAL_OBS_SYSTEM)
                .setValue(dto.getObservationId())
                .setUse(Identifier.IdentifierUse.USUAL)
        );

        // --- Status: always 'final' for submitted observations ---
        observation.setStatus(Observation.ObservationStatus.FINAL);

        // --- LOINC code for the observation type ---
        observation.setCode(buildObservationCode(dto.getObservationType()));

        // --- Subject (patient reference) ---
        observation.setSubject(
            new Reference(PATIENT_REFERENCE_PREFIX + dto.getPatientId())
        );

        // --- Effective date ---
        observation.setEffective(parseRecordedDate(dto.getRecordedDate()));

        // --- Value ---
        setObservationValue(observation, dto);

        return observation;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Returns a LOINC-coded {@link CodeableConcept} for the given observation type.
     */
    private CodeableConcept buildObservationCode(String observationType) {
        ObservationMeta meta = ObservationMeta.from(observationType);
        return new CodeableConcept()
                .addCoding(
                    new Coding()
                        .setSystem(LOINC_SYSTEM)
                        .setCode(meta.loincCode())
                        .setDisplay(meta.display())
                )
                .setText(meta.display());
    }

    /**
     * Sets the value element on the observation.
     *
     * <p>Blood pressure is modelled as a component-based observation (two components:
     * systolic and diastolic). All other observations use a single {@link Quantity}.</p>
     */
    private void setObservationValue(Observation observation, ObservationDTO dto) {
        String type = dto.getObservationType().toLowerCase();

        if ("blood-pressure".equals(type)) {
            buildBloodPressureComponents(observation, dto.getObservationValue(), dto.getUnit());
        } else {
            try {
                double value = Double.parseDouble(dto.getObservationValue());
                observation.setValue(
                    new Quantity()
                        .setValue(value)
                        .setUnit(dto.getUnit())
                        .setSystem("http://unitsofmeasure.org")
                        .setCode(dto.getUnit())
                );
            } catch (NumberFormatException ex) {
                // Fallback: encode as string if value is not numeric
                observation.setValue(new StringType(dto.getObservationValue()));
            }
        }
    }

    /**
     * Builds two FHIR observation components for a blood-pressure reading.
     *
     * <p>Input format: {@code "systolic/diastolic"} — e.g. {@code "120/80"}.</p>
     */
    private void buildBloodPressureComponents(Observation observation,
                                              String bpValue, String unit) {
        String[] parts = bpValue.split("/");
        if (parts.length != 2) {
            // Treat as a plain string value if format is unexpected
            observation.setValue(new StringType(bpValue));
            return;
        }

        // Add a parent code for the panel
        observation.setCode(
            new CodeableConcept()
                .addCoding(new Coding()
                    .setSystem(LOINC_SYSTEM)
                    .setCode("55284-4")
                    .setDisplay("Blood pressure systolic and diastolic"))
                .setText("Blood pressure systolic and diastolic")
        );

        // Systolic component — LOINC 8480-6
        Observation.ObservationComponentComponent systolic =
            new Observation.ObservationComponentComponent();
        systolic.setCode(new CodeableConcept()
            .addCoding(new Coding()
                .setSystem(LOINC_SYSTEM)
                .setCode("8480-6")
                .setDisplay("Systolic blood pressure"))
            .setText("Systolic blood pressure"));
        try {
            systolic.setValue(new Quantity()
                .setValue(Double.parseDouble(parts[0].trim()))
                .setUnit(unit)
                .setSystem("http://unitsofmeasure.org")
                .setCode(unit));
        } catch (NumberFormatException e) {
            systolic.setValue(new StringType(parts[0].trim()));
        }
        observation.addComponent(systolic);

        // Diastolic component — LOINC 8462-4
        Observation.ObservationComponentComponent diastolic =
            new Observation.ObservationComponentComponent();
        diastolic.setCode(new CodeableConcept()
            .addCoding(new Coding()
                .setSystem(LOINC_SYSTEM)
                .setCode("8462-4")
                .setDisplay("Diastolic blood pressure"))
            .setText("Diastolic blood pressure"));
        try {
            diastolic.setValue(new Quantity()
                .setValue(Double.parseDouble(parts[1].trim()))
                .setUnit(unit)
                .setSystem("http://unitsofmeasure.org")
                .setCode(unit));
        } catch (NumberFormatException e) {
            diastolic.setValue(new StringType(parts[1].trim()));
        }
        observation.addComponent(diastolic);
    }

    /**
     * Parses an ISO-8601 date string into a FHIR {@link DateTimeType}.
     *
     * @throws InvalidDateException if the date string is not a valid calendar date
     */
    private DateTimeType parseRecordedDate(String recordedDate) {
        try {
            LocalDate.parse(recordedDate);
            return new DateTimeType(recordedDate);
        } catch (DateTimeParseException ex) {
            throw new InvalidDateException(
                "Invalid recordedDate value: '" + recordedDate + "'. Expected yyyy-MM-dd.");
        }
    }

    // -------------------------------------------------------------------------
    // Observation metadata enum
    // -------------------------------------------------------------------------

    /**
     * Maps each supported observation type to its LOINC code and display name.
     */
    private enum ObservationMeta {
        BLOOD_PRESSURE("blood-pressure",   "55284-4", "Blood pressure systolic and diastolic"),
        TEMPERATURE(   "temperature",       "8310-5",  "Body temperature"),
        HEART_RATE(    "heart-rate",        "8867-4",  "Heart rate"),
        WEIGHT(        "weight",            "29463-7", "Body weight"),
        HEIGHT(        "height",            "8302-2",  "Body height"),
        OXYGEN_SAT(    "oxygen-saturation", "2708-6",  "Oxygen saturation");

        private final String key;
        private final String loinc;
        private final String display;

        ObservationMeta(String key, String loinc, String display) {
            this.key = key;
            this.loinc = loinc;
            this.display = display;
        }

        public String loincCode() { return loinc; }
        public String display()   { return display; }

        public static ObservationMeta from(String type) {
            for (ObservationMeta m : values()) {
                if (m.key.equalsIgnoreCase(type)) return m;
            }
            throw new IllegalArgumentException(
                "Unknown observation type: '" + type + "'");
        }
    }
}
