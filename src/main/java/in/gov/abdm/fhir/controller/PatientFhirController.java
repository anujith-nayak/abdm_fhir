package in.gov.abdm.fhir.controller;

import in.gov.abdm.fhir.dto.PatientDTO;
import in.gov.abdm.fhir.service.PatientFhirService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing the FHIR Patient conversion endpoint.
 *
 * <p>Accepts hospital patient data as JSON, validates it, converts it to a
 * genuine HL7 FHIR R4 Patient resource, and returns it as {@code application/fhir+json}.</p>
 *
 * <p>This controller intentionally contains no business logic — it delegates
 * entirely to {@link PatientFhirService}.</p>
 */
@RestController
@RequestMapping("/fhir")
public class PatientFhirController {

    private final PatientFhirService patientFhirService;

    /**
     * Constructs the controller with its required service via constructor injection.
     *
     * @param patientFhirService the service that orchestrates FHIR conversion
     */
    public PatientFhirController(PatientFhirService patientFhirService) {
        this.patientFhirService = patientFhirService;
    }

    /**
     * Converts hospital patient data to an HL7 FHIR R4 Patient resource.
     *
     * <p><strong>Endpoint:</strong> {@code POST /fhir/patient}</p>
     * <p><strong>Request:</strong> {@code application/json} — a {@link PatientDTO}</p>
     * <p><strong>Response:</strong> {@code application/fhir+json} — a FHIR R4 Patient resource</p>
     *
     * @param patientDTO the incoming patient data; validated via Bean Validation
     * @return 200 OK with the FHIR Patient JSON body, or 400/500 on error
     */
    @PostMapping(
            value = "/patient",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = "application/fhir+json"
    )
    public ResponseEntity<String> convertPatient(@Valid @RequestBody PatientDTO patientDTO) {
        String fhirJson = patientFhirService.convertToFhirPatientJson(patientDTO);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("application/fhir+json"))
                .body(fhirJson);
    }
}
