package in.gov.abdm.fhir.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.abdm.fhir.dto.DiagnosticReportDTO;
import in.gov.abdm.fhir.dto.MedicationRequestDTO;
import in.gov.abdm.fhir.dto.ObservationDTO;
import in.gov.abdm.fhir.dto.PatientDTO;
import in.gov.abdm.fhir.dto.PatientRecordDTO;
import in.gov.abdm.fhir.dto.PractitionerDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link in.gov.abdm.fhir.bundle.BundleController}.
 *
 * <p>Example request: {@code src/test/resources/bundle/example-request.json}</p>
 * <p>Postman collection: {@code src/test/resources/bundle/postman-collection.json}</p>
 *
 * <p>Example Bundle response structure (200 OK):</p>
 * <pre>{@code
 * {
 *   "resourceType": "Bundle",
 *   "type": "collection",
 *   "entry": [
 *     { "fullUrl": "Patient/PAT-001", "resource": { "resourceType": "Patient", "id": "PAT-001", ... } },
 *     { "fullUrl": "Practitioner/PRAC-001", "resource": { "resourceType": "Practitioner", ... } },
 *     { "fullUrl": "Observation/OBS-001", "resource": { "resourceType": "Observation",
 *         "subject": { "reference": "Patient/PAT-001" }, ... } },
 *     { "fullUrl": "MedicationRequest/RX-001", "resource": { "resourceType": "MedicationRequest",
 *         "subject": { "reference": "Patient/PAT-001" },
 *         "requester": { "reference": "Practitioner/PRAC-001" }, ... } },
 *     { "fullUrl": "DiagnosticReport/RPT-001", "resource": { "resourceType": "DiagnosticReport",
 *         "subject": { "reference": "Patient/PAT-001" },
 *         "performer": [{ "reference": "Practitioner/PRAC-001" }], ... } }
 *   ]
 * }
 * }</pre>
 */
@SpringBootTest
@AutoConfigureMockMvc
class BundleControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /fhir/bundle - example request file returns FHIR R4 Bundle JSON")
    void givenExampleRequestFile_whenConvert_thenReturnsFhirBundleJson() throws Exception {
        String requestJson = new ClassPathResource("bundle/example-request.json")
                .getContentAsString(StandardCharsets.UTF_8);

        mockMvc.perform(post("/fhir/bundle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/fhir+json"))
                .andExpect(jsonPath("$.resourceType").value("Bundle"))
                .andExpect(jsonPath("$.type").value("collection"))
                .andExpect(jsonPath("$.entry.length()").value(6))
                .andExpect(jsonPath("$.entry[0].fullUrl").value("Patient/PAT-001"))
                .andExpect(jsonPath("$.entry[0].resource.resourceType").value("Patient"))
                .andExpect(jsonPath("$.entry[0].resource.id").value("PAT-001"))
                .andExpect(jsonPath("$.entry[1].fullUrl").value("Practitioner/PRAC-001"))
                .andExpect(jsonPath("$.entry[1].resource.resourceType").value("Practitioner"))
                .andExpect(jsonPath("$.entry[2].resource.resourceType").value("Observation"))
                .andExpect(jsonPath("$.entry[2].resource.subject.reference").value("Patient/PAT-001"))
                .andExpect(jsonPath("$.entry[3].resource.resourceType").value("Observation"))
                .andExpect(jsonPath("$.entry[3].resource.subject.reference").value("Patient/PAT-001"))
                .andExpect(jsonPath("$.entry[4].resource.resourceType").value("MedicationRequest"))
                .andExpect(jsonPath("$.entry[4].resource.subject.reference").value("Patient/PAT-001"))
                .andExpect(jsonPath("$.entry[4].resource.requester.reference").value("Practitioner/PRAC-001"))
                .andExpect(jsonPath("$.entry[5].resource.resourceType").value("DiagnosticReport"))
                .andExpect(jsonPath("$.entry[5].resource.subject.reference").value("Patient/PAT-001"))
                .andExpect(jsonPath("$.entry[5].resource.performer[0].reference").value("Practitioner/PRAC-001"));
    }

    @Test
    @DisplayName("POST /fhir/bundle - complete record built programmatically returns valid Bundle")
    void givenCompletePatientRecord_whenConvert_thenReturnsFhirBundleJson() throws Exception {
        PatientRecordDTO record = buildCompleteRecord();

        mockMvc.perform(post("/fhir/bundle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(record)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resourceType").value("Bundle"))
                .andExpect(jsonPath("$.type").value("collection"))
                .andExpect(jsonPath("$.entry.length()").value(5));
    }

    @Test
    @DisplayName("POST /fhir/bundle - patient and practitioner only with empty lists returns valid Bundle")
    void givenMinimalRecordWithEmptyLists_whenConvert_thenReturnsFhirBundleJson() throws Exception {
        PatientRecordDTO record = PatientRecordDTO.builder()
                .patient(PatientDTO.builder()
                        .patientId("PAT-010")
                        .fullName("Sunita Devi")
                        .gender("female")
                        .dateOfBirth("1975-11-30")
                        .build())
                .practitioner(PractitionerDTO.builder()
                        .practitionerId("PRAC-010")
                        .fullName("Dr. Rajesh Nair")
                        .build())
                .observations(List.of())
                .medications(List.of())
                .diagnosticReports(List.of())
                .build();

        mockMvc.perform(post("/fhir/bundle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(record)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resourceType").value("Bundle"))
                .andExpect(jsonPath("$.entry.length()").value(2))
                .andExpect(jsonPath("$.entry[0].resource.resourceType").value("Patient"))
                .andExpect(jsonPath("$.entry[1].resource.resourceType").value("Practitioner"));
    }

    @Test
    @DisplayName("POST /fhir/bundle - missing patient returns 400")
    void givenMissingPatient_whenConvert_thenReturns400() throws Exception {
        PatientRecordDTO record = PatientRecordDTO.builder()
                .practitioner(PractitionerDTO.builder()
                        .practitionerId("PRAC-001")
                        .fullName("Dr. Ananya Mehta")
                        .build())
                .build();

        mockMvc.perform(post("/fhir/bundle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(record)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    @DisplayName("POST /fhir/bundle - missing practitioner returns 400")
    void givenMissingPractitioner_whenConvert_thenReturns400() throws Exception {
        PatientRecordDTO record = PatientRecordDTO.builder()
                .patient(PatientDTO.builder()
                        .patientId("PAT-001")
                        .fullName("Ravi Kumar")
                        .gender("male")
                        .dateOfBirth("1990-05-15")
                        .build())
                .build();

        mockMvc.perform(post("/fhir/bundle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(record)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    @DisplayName("POST /fhir/bundle - invalid nested observation returns 400")
    void givenInvalidNestedObservation_whenConvert_thenReturns400() throws Exception {
        PatientRecordDTO record = PatientRecordDTO.builder()
                .patient(PatientDTO.builder()
                        .patientId("PAT-001")
                        .fullName("Ravi Kumar")
                        .gender("male")
                        .dateOfBirth("1990-05-15")
                        .build())
                .practitioner(PractitionerDTO.builder()
                        .practitionerId("PRAC-001")
                        .fullName("Dr. Ananya Mehta")
                        .build())
                .observations(List.of(
                        ObservationDTO.builder()
                                .observationId("OBS-001")
                                .patientId("PAT-001")
                                .observationType("invalid-type")
                                .observationValue("72")
                                .unit("bpm")
                                .recordedDate("2024-11-01")
                                .build()
                ))
                .build();

        mockMvc.perform(post("/fhir/bundle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(record)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    private PatientRecordDTO buildCompleteRecord() {
        return PatientRecordDTO.builder()
                .patient(PatientDTO.builder()
                        .patientId("PAT-001")
                        .fullName("Ravi Kumar")
                        .gender("male")
                        .dateOfBirth("1990-05-15")
                        .build())
                .practitioner(PractitionerDTO.builder()
                        .practitionerId("PRAC-001")
                        .fullName("Dr. Ananya Mehta")
                        .specialization("General Medicine")
                        .build())
                .observations(List.of(
                        ObservationDTO.builder()
                                .observationId("OBS-001")
                                .patientId("PAT-001")
                                .observationType("heart-rate")
                                .observationValue("72")
                                .unit("bpm")
                                .recordedDate("2024-11-01")
                                .build()
                ))
                .medications(List.of(
                        MedicationRequestDTO.builder()
                                .prescriptionId("RX-001")
                                .patientId("PAT-001")
                                .medicineName("Paracetamol 500mg")
                                .dosage("500 mg")
                                .frequency("twice daily")
                                .duration("5 days")
                                .build()
                ))
                .diagnosticReports(List.of(
                        DiagnosticReportDTO.builder()
                                .reportId("RPT-001")
                                .patientId("PAT-001")
                                .reportName("Complete Blood Count")
                                .reportStatus("final")
                                .reportDate("2024-11-01")
                                .build()
                ))
                .build();
    }
}
