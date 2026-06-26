package in.gov.abdm.fhir.controller;

import in.gov.abdm.fhir.dto.DiagnosticReportDTO;
import in.gov.abdm.fhir.service.DiagnosticReportFhirService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing the FHIR DiagnosticReport conversion endpoint.
 *
 * <p>Accepts hospital diagnostic report data as JSON, validates it, converts it to a
 * genuine HL7 FHIR R4 DiagnosticReport resource, and returns it as {@code application/fhir+json}.</p>
 */
@RestController
@RequestMapping("/fhir")
public class DiagnosticReportFhirController {

    private final DiagnosticReportFhirService diagnosticReportFhirService;

    public DiagnosticReportFhirController(DiagnosticReportFhirService diagnosticReportFhirService) {
        this.diagnosticReportFhirService = diagnosticReportFhirService;
    }

    /**
     * Converts hospital diagnostic report data to an HL7 FHIR R4 DiagnosticReport resource.
     *
     * <p><strong>Endpoint:</strong> {@code POST /fhir/diagnostic-report}</p>
     * <p><strong>Request:</strong> {@code application/json} — a {@link DiagnosticReportDTO}</p>
     * <p><strong>Response:</strong> {@code application/fhir+json} — a FHIR R4 DiagnosticReport resource</p>
     *
     * @param diagnosticReportDTO the incoming report data; validated via Bean Validation
     * @return 200 OK with the FHIR DiagnosticReport JSON body, or 400/500 on error
     */
    @PostMapping(
            value = "/diagnostic-report",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = "application/fhir+json"
    )
    public ResponseEntity<String> convertDiagnosticReport(
            @Valid @RequestBody DiagnosticReportDTO diagnosticReportDTO) {
        String fhirJson = diagnosticReportFhirService
                .convertToFhirDiagnosticReportJson(diagnosticReportDTO);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("application/fhir+json"))
                .body(fhirJson);
    }
}
