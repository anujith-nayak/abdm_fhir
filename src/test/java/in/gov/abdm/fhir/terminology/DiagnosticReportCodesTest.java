package in.gov.abdm.fhir.terminology;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DiagnosticReportCodes}.
 */
class DiagnosticReportCodesTest {

    @Test
    @DisplayName("buildLabCategory returns HL7 v2-0074 LAB code")
    void buildLabCategory_returnsLabCode() {
        CodeableConcept category = DiagnosticReportCodes.buildLabCategory();
        assertEquals(FhirConstants.HL7_V2_0074, category.getCoding().get(0).getSystem());
        assertEquals("LAB", category.getCoding().get(0).getCode());
    }

    @Test
    @DisplayName("buildRadiologyCategory returns HL7 v2-0074 RAD code")
    void buildRadiologyCategory_returnsRadCode() {
        CodeableConcept category = DiagnosticReportCodes.buildRadiologyCategory();
        assertEquals("RAD", category.getCoding().get(0).getCode());
    }

    @ParameterizedTest(name = "report name ''{0}'' → category ''{1}''")
    @CsvSource({
        "Complete Blood Count,      LAB",
        "X-Ray Chest,               RAD",
        "MRI Brain,                 RAD",
        "CT Scan Abdomen,            RAD",
        "Biopsy Liver,              PAT",
        "ECG Report,                CUS",
        "Lipid Profile,             LAB"
    })
    @DisplayName("inferCategory maps report name keywords to correct category")
    void givenReportName_whenInferCategory_thenCorrectCode(
            String reportName, String expectedCode) {
        CodeableConcept category = DiagnosticReportCodes.inferCategory(reportName.trim());
        assertEquals(expectedCode.trim(), category.getCoding().get(0).getCode());
    }

    @Test
    @DisplayName("inferCategory falls back to LAB for unrecognised names")
    void givenUnknownReportName_whenInferCategory_thenFallsBackToLab() {
        CodeableConcept category = DiagnosticReportCodes.inferCategory("Unknown Test");
        assertEquals("LAB", category.getCoding().get(0).getCode());
    }

    @Test
    @DisplayName("buildTextOnlyReportType returns text-only CodeableConcept")
    void buildTextOnlyReportType_hasTextAndNoCoding() {
        CodeableConcept concept = DiagnosticReportCodes.buildTextOnlyReportType("CBC");
        assertEquals("CBC", concept.getText());
        assertTrue(concept.getCoding().isEmpty());
    }

    @Test
    @DisplayName("buildCodedReportType includes both coding and text")
    void buildCodedReportType_hasCodingAndText() {
        CodeableConcept concept = DiagnosticReportCodes.buildCodedReportType(
                "CBC", FhirConstants.LOINC, "58410-2", "CBC panel");
        assertEquals("CBC", concept.getText());
        assertEquals("58410-2", concept.getCoding().get(0).getCode());
        assertEquals(FhirConstants.LOINC, concept.getCoding().get(0).getSystem());
    }
}
