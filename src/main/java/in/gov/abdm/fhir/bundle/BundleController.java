package in.gov.abdm.fhir.bundle;

import in.gov.abdm.fhir.dto.PatientRecordDTO;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing the FHIR Bundle conversion endpoint.
 *
 * <p>Accepts a complete hospital record as JSON, validates it, converts every section
 * into FHIR resources, assembles them into a single Bundle, and returns it as
 * {@code application/fhir+json}.</p>
 */
@RestController
@RequestMapping("/fhir")
public class BundleController {

    private final BundleService bundleService;

    public BundleController(BundleService bundleService) {
        this.bundleService = bundleService;
    }

    /**
     * Converts a complete hospital record to an HL7 FHIR R4 Bundle.
     *
     * <p><strong>Endpoint:</strong> {@code POST /fhir/bundle}</p>
     * <p><strong>Request:</strong> {@code application/json} — a {@link PatientRecordDTO}</p>
     * <p><strong>Response:</strong> {@code application/fhir+json} — a FHIR R4 Bundle resource</p>
     *
     * @param record the incoming hospital record; validated via Bean Validation
     * @return 200 OK with the FHIR Bundle JSON body, or 400/500 on error
     */
    @PostMapping(
            value = "/bundle",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = "application/fhir+json"
    )
    public ResponseEntity<String> convertBundle(@Valid @RequestBody PatientRecordDTO record) {
        String fhirJson = bundleService.convertToFhirBundleJson(record);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("application/fhir+json"))
                .body(fhirJson);
    }
}
