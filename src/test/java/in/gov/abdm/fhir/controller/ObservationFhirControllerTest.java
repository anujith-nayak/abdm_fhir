package in.gov.abdm.fhir.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.abdm.fhir.dto.ObservationDTO;
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
class ObservationFhirControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /fhir/observation - heart rate returns FHIR Observation JSON")
    void givenHeartRateObservation_whenConvert_thenReturnsFhirJson() throws Exception {
        ObservationDTO dto = ObservationDTO.builder()
                .observationId("OBS-001")
                .patientId("PAT-001")
                .observationType("heart-rate")
                .observationValue("72")
                .unit("bpm")
                .recordedDate("2024-11-01")
                .build();

        mockMvc.perform(post("/fhir/observation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/fhir+json"))
                .andExpect(jsonPath("$.resourceType").value("Observation"))
                .andExpect(jsonPath("$.id").value("OBS-001"))
                .andExpect(jsonPath("$.status").value("final"));
    }

    @Test
    @DisplayName("POST /fhir/observation - blood pressure returns component-based Observation")
    void givenBloodPressureObservation_whenConvert_thenReturnsFhirJsonWithComponents() throws Exception {
        ObservationDTO dto = ObservationDTO.builder()
                .observationId("OBS-002")
                .patientId("PAT-001")
                .observationType("blood-pressure")
                .observationValue("120/80")
                .unit("mmHg")
                .recordedDate("2024-11-01")
                .build();

        mockMvc.perform(post("/fhir/observation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resourceType").value("Observation"))
                .andExpect(jsonPath("$.component").isArray())
                .andExpect(jsonPath("$.component.length()").value(2));
    }

    @Test
    @DisplayName("POST /fhir/observation - temperature returns FHIR Observation JSON")
    void givenTemperatureObservation_whenConvert_thenReturnsFhirJson() throws Exception {
        ObservationDTO dto = ObservationDTO.builder()
                .observationId("OBS-003")
                .patientId("PAT-002")
                .observationType("temperature")
                .observationValue("37.2")
                .unit("Cel")
                .recordedDate("2024-11-02")
                .build();

        mockMvc.perform(post("/fhir/observation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resourceType").value("Observation"))
                .andExpect(jsonPath("$.valueQuantity.value").value(37.2));
    }

    @Test
    @DisplayName("POST /fhir/observation - invalid observation type returns 400")
    void givenInvalidObservationType_whenConvert_thenReturns400() throws Exception {
        ObservationDTO dto = ObservationDTO.builder()
                .observationId("OBS-004")
                .patientId("PAT-001")
                .observationType("cholesterol")    // not in allowed list
                .observationValue("200")
                .unit("mg/dL")
                .recordedDate("2024-11-01")
                .build();

        mockMvc.perform(post("/fhir/observation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /fhir/observation - missing required fields returns 400")
    void givenMissingFields_whenConvert_thenReturns400() throws Exception {
        ObservationDTO dto = new ObservationDTO();

        mockMvc.perform(post("/fhir/observation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }
}
