package in.gov.abdm.fhir.ehr.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link EhrController}.
 *
 * <p>These tests run against the full Spring context with {@code ehr.provider=dummy},
 * which is the default. They verify the complete pipeline:
 * EhrController → EhrService → DummyHmsProvider → BundleService → FHIR Bundle JSON.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
class EhrControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /ehr/patient/{patientId}/bundle - valid PAT- ID returns FHIR Bundle")
    void givenValidPatientId_whenGetBundle_thenReturnsFhirBundle() throws Exception {
        mockMvc.perform(get("/ehr/patient/PAT-001/bundle"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/fhir+json"))
                .andExpect(jsonPath("$.resourceType").value("Bundle"))
                .andExpect(jsonPath("$.type").value("collection"))
                .andExpect(jsonPath("$.entry").isArray())
                // Should contain at least Patient + Practitioner entries
                .andExpect(jsonPath("$.entry.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
    }

    @Test
    @DisplayName("GET /ehr/patient/{patientId}/bundle - bundle contains Patient resource")
    void givenValidPatientId_whenGetBundle_thenBundleContainsPatient() throws Exception {
        mockMvc.perform(get("/ehr/patient/PAT-999/bundle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entry[0].resource.resourceType").value("Patient"));
    }

    @Test
    @DisplayName("GET /ehr/patient/{patientId}/bundle - bundle contains Practitioner resource")
    void givenValidPatientId_whenGetBundle_thenBundleContainsPractitioner() throws Exception {
        mockMvc.perform(get("/ehr/patient/PAT-123/bundle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entry[1].resource.resourceType").value("Practitioner"));
    }

    @Test
    @DisplayName("GET /ehr/patient/{patientId}/bundle - unknown patient ID returns 404")
    void givenUnknownPatientId_whenGetBundle_thenReturns404() throws Exception {
        mockMvc.perform(get("/ehr/patient/UNKNOWN-999/bundle"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Patient Not Found"));
    }
}
