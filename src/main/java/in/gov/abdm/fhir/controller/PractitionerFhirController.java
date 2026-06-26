package in.gov.abdm.fhir.controller;

import in.gov.abdm.fhir.dto.PractitionerDTO;
import in.gov.abdm.fhir.service.PractitionerFhirService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing the FHIR Practitioner conversion endpoint.
 *
 * <p>Accepts hospital practitioner data as JSON, validates it, converts it to a
 * genuine HL7 FHIR R4 Practitioner resource, and returns it as {@code application/fhir+json}.</p>
 */
@RestController
@RequestMapping("/fhir")
public class PractitionerFhirController {

    private final PractitionerFhirService practitionerFhirService;

    public PractitionerFhirController(PractitionerFhirService practitionerFhirService) {
        this.practitionerFhirService = practitionerFhirService;
    }

    /**
     * Converts hospital practitioner data to an HL7 FHIR R4 Practitioner resource.
     *
     * <p><strong>Endpoint:</strong> {@code POST /fhir/practitioner}</p>
     * <p><strong>Request:</strong> {@code application/json} — a {@link PractitionerDTO}</p>
     * <p><strong>Response:</strong> {@code application/fhir+json} — a FHIR R4 Practitioner resource</p>
     *
     * @param practitionerDTO the incoming practitioner data; validated via Bean Validation
     * @return 200 OK with the FHIR Practitioner JSON body, or 400/500 on error
     */
    @PostMapping(
            value = "/practitioner",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = "application/fhir+json"
    )
    public ResponseEntity<String> convertPractitioner(
            @Valid @RequestBody PractitionerDTO practitionerDTO) {
        String fhirJson = practitionerFhirService.convertToFhirPractitionerJson(practitionerDTO);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("application/fhir+json"))
                .body(fhirJson);
    }
}
