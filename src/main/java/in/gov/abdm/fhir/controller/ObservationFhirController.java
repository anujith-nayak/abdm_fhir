package in.gov.abdm.fhir.controller;

import in.gov.abdm.fhir.dto.ObservationDTO;
import in.gov.abdm.fhir.service.ObservationFhirService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing the FHIR Observation conversion endpoint.
 *
 * <p>Accepts clinical observation data as JSON, validates it, converts it to a
 * genuine HL7 FHIR R4 Observation resource, and returns it as {@code application/fhir+json}.</p>
 *
 * <p>Supported observation types: blood-pressure, temperature, heart-rate,
 * weight, height, oxygen-saturation.</p>
 */
@RestController
@RequestMapping("/fhir")
public class ObservationFhirController {

    private final ObservationFhirService observationFhirService;

    public ObservationFhirController(ObservationFhirService observationFhirService) {
        this.observationFhirService = observationFhirService;
    }

    /**
     * Converts clinical observation data to an HL7 FHIR R4 Observation resource.
     *
     * <p><strong>Endpoint:</strong> {@code POST /fhir/observation}</p>
     * <p><strong>Request:</strong> {@code application/json} — an {@link ObservationDTO}</p>
     * <p><strong>Response:</strong> {@code application/fhir+json} — a FHIR R4 Observation resource</p>
     *
     * @param observationDTO the incoming observation data; validated via Bean Validation
     * @return 200 OK with the FHIR Observation JSON body, or 400/500 on error
     */
    @PostMapping(
            value = "/observation",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = "application/fhir+json"
    )
    public ResponseEntity<String> convertObservation(
            @Valid @RequestBody ObservationDTO observationDTO) {
        String fhirJson = observationFhirService.convertToFhirObservationJson(observationDTO);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("application/fhir+json"))
                .body(fhirJson);
    }
}
