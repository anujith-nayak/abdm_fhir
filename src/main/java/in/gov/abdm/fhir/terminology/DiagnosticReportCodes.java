package in.gov.abdm.fhir.terminology;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

/**
 * Centralised coding registry for {@link org.hl7.fhir.r4.model.DiagnosticReport} resources
 * used across the ABDM FHIR Service.
 *
 * <h2>Category codes</h2>
 * <p>Report categories use the HL7 v2 Table 0074 coding system, which is the
 * standard referenced by the FHIR R4 DiagnosticReport profile.</p>
 *
 * <h2>Report type codes</h2>
 * <p>When a formal LOINC or SNOMED code exists for the report type it should be
 * supplied via {@link #buildCodedReportType}. Where no formal code exists the
 * text-only path {@link #buildTextOnlyReportType} is used (valid FHIR R4).</p>
 *
 * <p>TODO (Phase 4): Implement a lookup that maps common Indian diagnostic report
 * names to LOINC document codes and replace text-only calls with coded ones.</p>
 */
public final class DiagnosticReportCodes {

    private DiagnosticReportCodes() {}

    // =========================================================================
    // Category codes — HL7 v2 Table 0074
    // =========================================================================

    /**
     * Builds a {@link CodeableConcept} for the Laboratory category.
     *
     * <p>This is the default category for most diagnostic reports (blood tests,
     * urine tests, etc.). Code: {@code LAB}.</p>
     */
    public static CodeableConcept buildLabCategory() {
        return buildV2Category("LAB", "Laboratory");
    }

    /**
     * Builds a {@link CodeableConcept} for the Radiology category.
     * Code: {@code RAD}.
     */
    public static CodeableConcept buildRadiologyCategory() {
        return buildV2Category("RAD", "Radiology");
    }

    /**
     * Builds a {@link CodeableConcept} for the Pathology category.
     * Code: {@code PAT}.
     */
    public static CodeableConcept buildPathologyCategory() {
        return buildV2Category("PAT", "Pathology");
    }

    /**
     * Builds a {@link CodeableConcept} for the Cardiology category.
     * Code: {@code CUS} (Cardiac Ultrasound).
     */
    public static CodeableConcept buildCardiologyCategory() {
        return buildV2Category("CUS", "Cardiac Ultrasound");
    }

    /**
     * Infers the most appropriate HL7 v2-0074 category from a free-text report name.
     *
     * <p>Matching is case-insensitive and keyword-based. Falls back to
     * {@link #buildLabCategory()} if no keyword matches.</p>
     *
     * @param reportName the human-readable report name from the DTO
     * @return the best-matching category {@link CodeableConcept}
     */
    public static CodeableConcept inferCategory(String reportName) {
        if (reportName == null) return buildLabCategory();
        String lower = reportName.toLowerCase();
        if (lower.contains("x-ray") || lower.contains("xray")
                || lower.contains("mri") || lower.contains("ct scan")
                || lower.contains("ultrasound") || lower.contains("usg")) {
            return buildRadiologyCategory();
        }
        if (lower.contains("biopsy") || lower.contains("histopathology")
                || lower.contains("cytology")) {
            return buildPathologyCategory();
        }
        if (lower.contains("ecg") || lower.contains("echo")
                || lower.contains("cardiac")) {
            return buildCardiologyCategory();
        }
        return buildLabCategory();
    }

    // =========================================================================
    // Report type (code element)
    // =========================================================================

    /**
     * Builds a text-only {@link CodeableConcept} for a diagnostic report type.
     *
     * <p>This is the current production path. Fully valid FHIR R4.
     * Replace with {@link #buildCodedReportType} when LOINC/SNOMED codes are available.</p>
     *
     * @param reportName the free-text report name from the DTO
     * @return a text-only {@link CodeableConcept}
     */
    public static CodeableConcept buildTextOnlyReportType(String reportName) {
        return new CodeableConcept().setText(reportName);
    }

    /**
     * Builds a formally coded {@link CodeableConcept} for a diagnostic report type.
     *
     * <p>Use this when a LOINC document code (e.g. 58410-2 for CBC panel) or
     * SNOMED procedure code is known for the report.</p>
     *
     * @param reportName the human-readable label (used as {@code text})
     * @param system     the coding system URI ({@link FhirConstants#LOINC} or
     *                   {@link FhirConstants#SNOMED_CT})
     * @param code       the code within the system
     * @param display    the official display name for the code
     * @return a coded {@link CodeableConcept} with text fallback
     */
    public static CodeableConcept buildCodedReportType(String reportName,
                                                        String system,
                                                        String code,
                                                        String display) {
        return new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem(system)
                        .setCode(code)
                        .setDisplay(display))
                .setText(reportName);
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    private static CodeableConcept buildV2Category(String code, String display) {
        return new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem(FhirConstants.HL7_V2_0074)
                        .setCode(code)
                        .setDisplay(display))
                .setText(display);
    }
}
