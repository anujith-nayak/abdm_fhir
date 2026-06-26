package in.gov.abdm.fhir.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.abdm.fhir.dto.PractitionerDTO;
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
class PractitionerFhirControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /fhir/practitioner - full request returns FHIR R4 Practitioner JSON")
    void givenFullPractitionerDTO_whenConvert_thenReturnsFhirJson() throws Exception {
        PractitionerDTO dto = PractitionerDTO.builder()
                .practitionerId("PRAC-001")
                .fullName("Dr. Ananya Sharma")
                .specialization("Cardiology")
                .department("Cardiology OPD")
                .phoneNumber("9123456780")
                .email("ananya.sharma@hospital.example.org")
                .build();

        mockMvc.perform(post("/fhir/practitioner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/fhir+json"))
                .andExpect(jsonPath("$.resourceType").value("Practitioner"))
                .andExpect(jsonPath("$.id").value("PRAC-001"))
                .andExpect(jsonPath("$.name[0].text").value("Dr. Ananya Sharma"));
    }

    @Test
    @DisplayName("POST /fhir/practitioner - minimal request (no optional fields) succeeds")
    void givenMinimalPractitionerDTO_whenConvert_thenReturnsFhirJson() throws Exception {
        PractitionerDTO dto = PractitionerDTO.builder()
                .practitionerId("PRAC-002")
                .fullName("Dr. Vikram Nair")
                .build();

        mockMvc.perform(post("/fhir/practitioner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resourceType").value("Practitioner"));
    }

    @Test
    @DisplayName("POST /fhir/practitioner - missing required fields returns 400")
    void givenMissingRequiredFields_whenConvert_thenReturns400() throws Exception {
        PractitionerDTO dto = new PractitionerDTO();  // practitionerId and fullName missing

        mockMvc.perform(post("/fhir/practitioner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    @DisplayName("POST /fhir/practitioner - invalid email returns 400")
    void givenInvalidEmail_whenConvert_thenReturns400() throws Exception {
        PractitionerDTO dto = PractitionerDTO.builder()
                .practitionerId("PRAC-003")
                .fullName("Dr. Test")
                .email("not-a-valid-email")
                .build();

        mockMvc.perform(post("/fhir/practitioner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
