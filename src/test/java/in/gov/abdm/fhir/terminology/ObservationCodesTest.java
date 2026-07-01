package in.gov.abdm.fhir.terminology;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ObservationCodes}.
 */
class ObservationCodesTest {

    @ParameterizedTest(name = "{0} → LOINC {1}")
    @CsvSource({
        "blood-pressure,    55284-4",
        "temperature,       8310-5",
        "heart-rate,        8867-4",
        "weight,            29463-7",
        "height,            8302-2",
        "oxygen-saturation, 2708-6"
    })
    @DisplayName("buildCode returns correct LOINC code for each observation type")
    void givenObservationType_whenBuildCode_thenCorrectLoincCode(
            String type, String expectedLoinc) {
        CodeableConcept concept = ObservationCodes.buildCode(type.trim());

        assertNotNull(concept);
        assertFalse(concept.getCoding().isEmpty());
        assertEquals(FhirConstants.LOINC, concept.getCoding().get(0).getSystem());
        assertEquals(expectedLoinc.trim(), concept.getCoding().get(0).getCode());
    }

    @ParameterizedTest(name = "case-insensitive: {0}")
    @CsvSource({"HEART-RATE", "Heart-Rate", "BLOOD-PRESSURE", "TEMPERATURE"})
    @DisplayName("buildCode is case-insensitive")
    void givenUpperCaseType_whenBuildCode_thenSucceeds(String type) {
        assertDoesNotThrow(() -> ObservationCodes.buildCode(type));
    }

    @Test
    @DisplayName("buildCode throws for unknown type")
    void givenUnknownType_whenBuildCode_thenThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> ObservationCodes.buildCode("cholesterol"));
    }

    @Test
    @DisplayName("buildSystolicCode returns LOINC 8480-6")
    void buildSystolicCode_returnsCorrectCode() {
        CodeableConcept code = ObservationCodes.buildSystolicCode();
        assertEquals("8480-6", code.getCoding().get(0).getCode());
        assertEquals(FhirConstants.LOINC, code.getCoding().get(0).getSystem());
    }

    @Test
    @DisplayName("buildDiastolicCode returns LOINC 8462-4")
    void buildDiastolicCode_returnsCorrectCode() {
        CodeableConcept code = ObservationCodes.buildDiastolicCode();
        assertEquals("8462-4", code.getCoding().get(0).getCode());
    }
}
