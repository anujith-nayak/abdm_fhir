package in.gov.abdm.fhir.terminology;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

/**
 * Centralised LOINC code registry for all clinical observation types supported
 * by the ABDM FHIR Service.
 *
 * <p>Each entry in {@link ObservationType} carries:
 * <ul>
 *   <li>The DTO key used in {@code ObservationDTO.observationType}</li>
 *   <li>The canonical LOINC code</li>
 *   <li>The LOINC display name</li>
 *   <li>The recommended UCUM unit code</li>
 * </ul>
 * </p>
 *
 * <p>Blood-pressure panel components have their own dedicated constants
 * {@link #SYSTOLIC_CODE} and {@link #DIASTOLIC_CODE} because they are
 * expressed as child components of the panel observation, not as top-level codes.</p>
 *
 * <p>Reference: <a href="https://loinc.org/panels/vital-signs/">LOINC Vital Signs Panel</a></p>
 */
public final class ObservationCodes {

    private ObservationCodes() {}

    // =========================================================================
    // Blood pressure component codes
    // =========================================================================

    /** LOINC 8480-6 — Systolic blood pressure component code. */
    public static final String SYSTOLIC_LOINC        = "8480-6";
    public static final String SYSTOLIC_DISPLAY      = "Systolic blood pressure";

    /** LOINC 8462-4 — Diastolic blood pressure component code. */
    public static final String DIASTOLIC_LOINC       = "8462-4";
    public static final String DIASTOLIC_DISPLAY     = "Diastolic blood pressure";

    // =========================================================================
    // Factory methods
    // =========================================================================

    /**
     * Builds a LOINC-coded {@link CodeableConcept} for the given observation type key.
     *
     * @param observationType the DTO observation type string (case-insensitive)
     * @return a {@link CodeableConcept} with a LOINC {@link Coding} and display text
     * @throws IllegalArgumentException if the type is not registered
     */
    public static CodeableConcept buildCode(String observationType) {
        ObservationType meta = ObservationType.from(observationType);
        return new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem(FhirConstants.LOINC)
                        .setCode(meta.loincCode())
                        .setDisplay(meta.display()))
                .setText(meta.display());
    }

    /**
     * Returns the recommended UCUM unit code for an observation type.
     *
     * @param observationType the DTO observation type string (case-insensitive)
     * @return the UCUM unit string
     */
    public static String ucumUnit(String observationType) {
        return ObservationType.from(observationType).ucumUnit();
    }

    /**
     * Builds a LOINC-coded {@link CodeableConcept} for the systolic BP component.
     */
    public static CodeableConcept buildSystolicCode() {
        return new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem(FhirConstants.LOINC)
                        .setCode(SYSTOLIC_LOINC)
                        .setDisplay(SYSTOLIC_DISPLAY))
                .setText(SYSTOLIC_DISPLAY);
    }

    /**
     * Builds a LOINC-coded {@link CodeableConcept} for the diastolic BP component.
     */
    public static CodeableConcept buildDiastolicCode() {
        return new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem(FhirConstants.LOINC)
                        .setCode(DIASTOLIC_LOINC)
                        .setDisplay(DIASTOLIC_DISPLAY))
                .setText(DIASTOLIC_DISPLAY);
    }

    // =========================================================================
    // Observation type registry
    // =========================================================================

    /**
     * Maps each supported observation DTO key to its LOINC code, display name,
     * and recommended UCUM unit.
     */
    public enum ObservationType {

        BLOOD_PRESSURE(   "blood-pressure",    "55284-4", "Blood pressure systolic and diastolic", "mm[Hg]"),
        TEMPERATURE(      "temperature",        "8310-5",  "Body temperature",                      "Cel"),
        HEART_RATE(       "heart-rate",         "8867-4",  "Heart rate",                            "/min"),
        WEIGHT(           "weight",             "29463-7", "Body weight",                           "kg"),
        HEIGHT(           "height",             "8302-2",  "Body height",                           "cm"),
        OXYGEN_SATURATION("oxygen-saturation",  "2708-6",  "Oxygen saturation in Arterial blood",   "%");

        private final String key;
        private final String loinc;
        private final String display;
        private final String ucum;

        ObservationType(String key, String loinc, String display, String ucum) {
            this.key = key;
            this.loinc = loinc;
            this.display = display;
            this.ucum = ucum;
        }

        public String key()       { return key; }
        public String loincCode() { return loinc; }
        public String display()   { return display; }
        public String ucumUnit()  { return ucum; }

        /**
         * Looks up an {@link ObservationType} by its DTO key (case-insensitive).
         *
         * @param type the raw observation type string from the DTO
         * @return the matching {@link ObservationType}
         * @throws IllegalArgumentException if no match is found
         */
        public static ObservationType from(String type) {
            for (ObservationType ot : values()) {
                if (ot.key.equalsIgnoreCase(type)) return ot;
            }
            throw new IllegalArgumentException("Unknown observation type: '" + type + "'");
        }
    }
}
