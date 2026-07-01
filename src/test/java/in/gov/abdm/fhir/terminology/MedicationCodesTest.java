package in.gov.abdm.fhir.terminology;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MedicationCodes}.
 */
class MedicationCodesTest {

    @Test
    @DisplayName("buildTextOnlyConcept sets text and has no coding")
    void buildTextOnlyConcept_setTextOnly() {
        CodeableConcept concept = MedicationCodes.buildTextOnlyConcept("Paracetamol 500mg");
        assertEquals("Paracetamol 500mg", concept.getText());
        assertTrue(concept.getCoding().isEmpty());
    }

    @Test
    @DisplayName("buildCodedConcept includes both coding and text")
    void buildCodedConcept_hasCodingAndText() {
        CodeableConcept concept = MedicationCodes.buildCodedConcept(
                "Paracetamol", FhirConstants.RXNORM, "161", "Acetaminophen");
        assertEquals("Paracetamol", concept.getText());
        assertEquals("161", concept.getCoding().get(0).getCode());
        assertEquals(FhirConstants.RXNORM, concept.getCoding().get(0).getSystem());
        assertEquals("Acetaminophen", concept.getCoding().get(0).getDisplay());
    }

    @Test
    @DisplayName("buildAbdmCodedConcept uses ABDM system URI")
    void buildAbdmCodedConcept_usesAbdmSystem() {
        CodeableConcept concept = MedicationCodes.buildAbdmCodedConcept(
                "Amoxicillin", "ABDM-MED-001", "Amoxicillin Trihydrate");
        assertEquals(FhirConstants.ABDM_MEDICATION_SYSTEM,
                concept.getCoding().get(0).getSystem());
        assertEquals("ABDM-MED-001", concept.getCoding().get(0).getCode());
    }

    @Test
    @DisplayName("buildRxNormConcept uses RxNorm system URI")
    void buildRxNormConcept_usesRxNormSystem() {
        CodeableConcept concept = MedicationCodes.buildRxNormConcept(
                "Metformin", "860975", "Metformin 500 MG");
        assertEquals(FhirConstants.RXNORM, concept.getCoding().get(0).getSystem());
        assertEquals("860975", concept.getCoding().get(0).getCode());
    }
}
