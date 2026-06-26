package in.gov.abdm.fhir.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.abdm.fhir.dto.PatientDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link PatientFhirController}.
 *
 * <p>Starts the full Spring context to verify the complete conversion pipeline:
 * validation → mapping → serialisation → HTTP response.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
class PatientFhirControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /fhir/patient - valid request returns FHIR R4 Patient JSON")
    void givenValidPatientDTO_whenConvert_thenReturnsFhirPatientJson() throws Exception {
        PatientDTO dto = PatientDTO.builder()
                .patientId("PAT-001")
                .fullName("Ravi Kumar")
                .gender("male")
                .dateOfBirth("1990-05-15")
                .phoneNumber("9876543210")
                .address("12, MG Road, Bengaluru, Karnataka 560001")
                .abhaNumber("91-1234-5678-9012")
                .build();

        mockMvc.perform(post("/fhir/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/fhir+json"))
                .andExpect(jsonPath("$.resourceType").value("Patient"))
                .andExpect(jsonPath("$.id").value("PAT-001"))
                .andExpect(jsonPath("$.gender").value("male"));
    }

    @Test
    @DisplayName("POST /fhir/patient - missing required fields returns 400")
    void givenMissingRequiredFields_whenConvert_thenReturns400() throws Exception {
        PatientDTO dto = PatientDTO.builder()
                // fullName, gender, dateOfBirth intentionally omitted
                .patientId("PAT-002")
                .build();

        mockMvc.perform(post("/fhir/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    @DisplayName("POST /fhir/patient - invalid gender returns 400")
    void givenInvalidGender_whenConvert_thenReturns400() throws Exception {
        PatientDTO dto = PatientDTO.builder()
                .patientId("PAT-003")
                .fullName("Priya Sharma")
                .gender("INVALID")
                .dateOfBirth("1985-03-20")
                .build();

        mockMvc.perform(post("/fhir/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /fhir/patient - invalid date format returns 400")
    void givenInvalidDateFormat_whenConvert_thenReturns400() throws Exception {
        PatientDTO dto = PatientDTO.builder()
                .patientId("PAT-004")
                .fullName("Amit Singh")
                .gender("male")
                .dateOfBirth("15-05-1990") // wrong format
                .build();

        mockMvc.perform(post("/fhir/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /fhir/patient - optional fields absent still produces valid FHIR")
    void givenMinimalPatientDTO_whenConvert_thenReturnsFhirPatientJson() throws Exception {
        PatientDTO dto = PatientDTO.builder()
                .patientId("PAT-005")
                .fullName("Sunita Devi")
                .gender("female")
                .dateOfBirth("1975-11-30")
                .build();

        mockMvc.perform(post("/fhir/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resourceType").value("Patient"))
                .andExpect(jsonPath("$.gender").value("female"));
    }
}
