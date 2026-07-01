package in.gov.abdm.fhir.terminology;

/**
 * Centralised registry of all coding system URIs used across the ABDM FHIR Service.
 *
 * <p>Every system URL that appears inside a {@code Coding.system} element must be
 * defined here. Mappers must never hard-code system strings inline — they import
 * from this class instead.</p>
 *
 * <p>Conventions used:
 * <ul>
 *   <li>International standards (LOINC, SNOMED, RxNorm, UCUM, HL7) use their
 *       canonical URIs as published by the respective SDO.</li>
 *   <li>ABDM-specific systems use the {@code https://ndhm.gov.in/} namespace.</li>
 *   <li>Hospital-local systems use {@code https://hospital.example.org/} as a
 *       placeholder; replace with the real OID or NamingSystem URI before go-live.</li>
 * </ul>
 * </p>
 */
public final class FhirConstants {

    private FhirConstants() {}

    // =========================================================================
    // Standard coding systems
    // =========================================================================

    /** LOINC — Logical Observation Identifiers Names and Codes. */
    public static final String LOINC = "http://loinc.org";

    /** SNOMED CT — Systematized Nomenclature of Medicine Clinical Terms. */
    public static final String SNOMED_CT = "http://snomed.info/sct";

    /**
     * RxNorm — US National Library of Medicine normalised drug codes.
     * Used as a fallback medication coding system where ABDM codes are unavailable.
     */
    public static final String RXNORM = "http://www.nlm.nih.gov/research/umls/rxnorm";

    /** UCUM — Unified Code for Units of Measure. */
    public static final String UCUM = "http://unitsofmeasure.org";

    /** HL7 v2 Table 0074 — Diagnostic service section identifier (e.g. LAB, RAD). */
    public static final String HL7_V2_0074 = "http://terminology.hl7.org/CodeSystem/v2-0074";

    /** HL7 observation-category value set. */
    public static final String HL7_OBSERVATION_CATEGORY =
            "http://terminology.hl7.org/CodeSystem/observation-category";

    // =========================================================================
    // ABDM / NHA-specific coding systems
    // =========================================================================

    /**
     * ABHA (Ayushman Bharat Health Account) identifier system.
     * Used as the {@code Identifier.system} for ABHA numbers on Patient resources.
     */
    public static final String ABHA_SYSTEM = "https://healthid.ndhm.gov.in";

    /**
     * ABDM-approved medication coding system.
     *
     * <p>Placeholder — replace with the actual NHA/ABDM medication CodeSystem URL
     * when published. Until then, medication concepts are carried as text-only
     * {@code CodeableConcept} entries.</p>
     *
     * <p>TODO (Phase 4): Replace with official ABDM drug code system URI once
     * the ABDM Terminology Service is available.</p>
     */
    public static final String ABDM_MEDICATION_SYSTEM = "https://ndhm.gov.in/medication-codes";

    /**
     * ABDM-approved diagnostic report category coding system.
     *
     * <p>Placeholder — replace with the official NHA CodeSystem URI on go-live.</p>
     *
     * <p>TODO (Phase 4): Replace with official ABDM diagnostic category system URI.</p>
     */
    public static final String ABDM_REPORT_CATEGORY_SYSTEM = "https://ndhm.gov.in/report-categories";

    // =========================================================================
    // Hospital-local identifier systems (replace before go-live)
    // =========================================================================

    /** Hospital-assigned patient identifier namespace. */
    public static final String HOSPITAL_PATIENT_SYSTEM = "https://hospital.example.org/patients";

    /** Hospital-assigned practitioner identifier namespace. */
    public static final String HOSPITAL_PRACTITIONER_SYSTEM = "https://hospital.example.org/practitioners";

    /** Hospital-assigned observation identifier namespace. */
    public static final String HOSPITAL_OBSERVATION_SYSTEM = "https://hospital.example.org/observations";

    /** Hospital-assigned prescription identifier namespace. */
    public static final String HOSPITAL_PRESCRIPTION_SYSTEM = "https://hospital.example.org/prescriptions";

    /** Hospital-assigned diagnostic report identifier namespace. */
    public static final String HOSPITAL_REPORT_SYSTEM = "https://hospital.example.org/reports";

    // =========================================================================
    // Common reference prefixes
    // =========================================================================

    /** Relative reference prefix for Patient resources. */
    public static final String PATIENT_REF_PREFIX = "Patient/";

    /** Relative reference prefix for Practitioner resources. */
    public static final String PRACTITIONER_REF_PREFIX = "Practitioner/";
}
