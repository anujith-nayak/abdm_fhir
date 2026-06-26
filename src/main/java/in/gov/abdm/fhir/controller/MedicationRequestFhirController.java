package in.gov.abdm.fhir.controller;

import in.gov.abdm.fhir.dto.MedicationRequestDTO;
import in.gov.abdm.fhir.service.MedicationRequestFhirService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing the FHIR MedicationRequest conversion endpoint.
 *
 * <p>Accepts hospital prescription data as JSON, validates it, converts it to a
 * genuine HL7 FHIR R4 MedicationRequest resource, and returns it as {@code application/fhir+json}.</p>
 */
@RestController
@RequestMapping("/fhir")
public class MedicationRequestFhirController {

    private final MedicationRequestFhirService medicationRequestFhirService;

    public MedicationRequestFhirController(MedicationRequestFhirService medicationRequestFhirService) {
        this.medicationRequestFhirService = medicationRequestFhirService;
    }

    /**
     * Converts hospital prescription data to an HL7 FHIR R4 MedicationRequest resource.
     *
     * <p><strong>Endpoint:</strong> {@code POST /fhir/medication}</p>
     * <p><strong>Request:</strong> {@code application/json} — a {@link MedicationRequestDTO}</p>
     * <p><strong>Response:</strong> {@code application/fhir+json} — a FHIR R4 MedicationRequest resource</p>
     *
     * @param medicationRequestDTO the incoming prescription data; validated via Bean Validation
     * @return 200 OK with the FHIR MedicationRequest JSON body, or 400/500 on error
     */
    @PostMapping(
            value = "/medication",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = "application/fhir+json"
    )
    public ResponseEntity<String> convertMedicationRequest(
            @Valid @RequestBody MedicationRequestDTO medicationRequestDTO) {
        String fhirJson = medicationRequestFhirService
                .convertToFhirMedicationRequestJson(medicationRequestDTO);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("application/fhir+json"))
                .body(fhirJson);
    }
}
