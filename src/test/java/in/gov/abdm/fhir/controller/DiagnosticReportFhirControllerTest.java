package in.gov.abdm.fhir.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.abdm.fhir.dto.DiagnosticReportDTO;
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
class DiagnosticReportFhirControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /fhir/diagnostic-report - full request returns FHIR DiagnosticReport JSON")
    void givenFullDiagnosticReportDTO_whenConvert_thenReturnsFhirJson() throws Exception {
        DiagnosticReportDTO dto = DiagnosticReportDTO.builder()
                .reportId("RPT-001")
                .patientId("PAT-001")
                .reportName("Complete Blood Count")
                .reportStatus("final")
                .conclusion("All values within normal range.")
                .reportDate("2024-11-01")
                .build();

        mockMvc.perform(post("/fhir/diagnostic-report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/fhir+json"))
                .andExpect(jsonPath("$.resourceType").value("DiagnosticReport"))
                .andExpect(jsonPath("$.id").value("RPT-001"))
                .andExpect(jsonPath("$.status").value("final"))
                .andExpect(jsonPath("$.conclusion").value("All values within normal range."));
    }

    @Test
    @DisplayName("POST /fhir/diagnostic-report - without conclusion still produces valid FHIR")
    void givenDtoWithoutConclusion_whenConvert_thenReturnsFhirJson() throws Exception {
        DiagnosticReportDTO dto = DiagnosticReportDTO.builder()
                .reportId("RPT-002")
                .patientId("PAT-002")
                .reportName("Lipid Profile")
                .reportStatus("preliminary")
                .reportDate("2024-11-02")
                .build();

        mockMvc.perform(post("/fhir/diagnostic-report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resourceType").value("DiagnosticReport"))
                .andExpect(jsonPath("$.status").value("preliminary"));
    }

    @Test
    @DisplayName("POST /fhir/diagnostic-report - invalid status returns 400")
    void givenInvalidStatus_whenConvert_thenReturns400() throws Exception {
        DiagnosticReportDTO dto = DiagnosticReportDTO.builder()
                .reportId("RPT-003")
                .patientId("PAT-001")
                .reportName("X-Ray Chest")
                .reportStatus("approved")          // not a valid FHIR status
                .reportDate("2024-11-01")
                .build();

        mockMvc.perform(post("/fhir/diagnostic-report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /fhir/diagnostic-report - missing required fields returns 400")
    void givenMissingRequiredFields_whenConvert_thenReturns400() throws Exception {
        DiagnosticReportDTO dto = new DiagnosticReportDTO();

        mockMvc.perform(post("/fhir/diagnostic-report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    @DisplayName("POST /fhir/diagnostic-report - invalid date format returns 400")
    void givenInvalidDateFormat_whenConvert_thenReturns400() throws Exception {
        DiagnosticReportDTO dto = DiagnosticReportDTO.builder()
                .reportId("RPT-004")
                .patientId("PAT-001")
                .reportName("MRI Brain")
                .reportStatus("final")
                .reportDate("01-11-2024")           // wrong format
                .build();

        mockMvc.perform(post("/fhir/diagnostic-report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
