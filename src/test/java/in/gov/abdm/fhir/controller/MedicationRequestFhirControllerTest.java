package in.gov.abdm.fhir.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.abdm.fhir.dto.MedicationRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MedicationRequestFhirControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /fhir/medication - full request returns FHIR MedicationRequest JSON")
    void givenFullMedicationRequestDTO_whenConvert_thenReturnsFhirJson() throws Exception {
        MedicationRequestDTO dto = MedicationRequestDTO.builder()
                .prescriptionId("RX-001")
                .patientId("PAT-001")
                .medicineName("Paracetamol 500mg")
                .dosage("500 mg")
                .frequency("twice daily")
                .duration("5 days")
                .instructions("Take after food with warm water")
                .build();

        mockMvc.perform(post("/fhir/medication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/fhir+json"))
                .andExpect(jsonPath("$.resourceType").value("MedicationRequest"))
                .andExpect(jsonPath("$.id").value("RX-001"))
                .andExpect(jsonPath("$.status").value("active"))
                .andExpect(jsonPath("$.intent").value("order"));
    }

    @Test
    @DisplayName("POST /fhir/medication - without instructions still produces valid FHIR")
    void givenDtoWithoutInstructions_whenConvert_thenReturnsFhirJson() throws Exception {
        MedicationRequestDTO dto = MedicationRequestDTO.builder()
                .prescriptionId("RX-002")
                .patientId("PAT-002")
                .medicineName("Amoxicillin 250mg")
                .dosage("250 mg")
                .frequency("three times daily")
                .duration("7 days")
                .build();

        mockMvc.perform(post("/fhir/medication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resourceType").value("MedicationRequest"));
    }

    @Test
    @DisplayName("POST /fhir/medication - missing required fields returns 400")
    void givenMissingRequiredFields_whenConvert_thenReturns400() throws Exception {
        MedicationRequestDTO dto = new MedicationRequestDTO();

        mockMvc.perform(post("/fhir/medication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }
}
