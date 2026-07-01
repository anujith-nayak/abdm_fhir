package in.gov.abdm.fhir.ehr.controller;

import in.gov.abdm.fhir.bundle.BundleService;
import in.gov.abdm.fhir.dto.PatientRecordDTO;
import in.gov.abdm.fhir.ehr.service.EhrService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for the EHR abstraction layer.
 *
 * <p>Exposes a single endpoint that fetches a complete patient record from the
 * configured HMS provider and returns it as a standards-compliant HL7 FHIR R4
 * Bundle.</p>
 *
 * <h2>Request flow</h2>
 * <pre>
 *   GET /ehr/patient/{patientId}/bundle
 *         │
 *         ▼
 *   EhrController
 *         │
 *         ▼
 *   EhrService → EhrDataProvider (Dummy / Real / REST / DB)
 *         │
 *         ▼  PatientRecordDTO
 *   BundleService  (existing — untouched)
 *         │
 *         ▼
 *   FHIR Bundle JSON
 * </pre>
 *
 * <p>This controller reuses the existing {@link BundleService} without duplicating
 * any bundle generation logic.</p>
 *
 * <p><strong>Dependency rule:</strong> This controller knows about {@link EhrService}
 * and {@link BundleService} only. It must never import FHIR mapper classes or
 * provider implementations directly.</p>
 */
@RestController
@RequestMapping("/ehr")
public class EhrController {

    private final EhrService ehrService;
    private final BundleService bundleService;

    /**
     * Constructor injection — both dependencies are Spring-managed beans.
     *
     * @param ehrService    the EHR service that retrieves patient records from the HMS
     * @param bundleService the existing FHIR bundle assembly service
     */
    public EhrController(EhrService ehrService, BundleService bundleService) {
        this.ehrService = ehrService;
        this.bundleService = bundleService;
    }

    /**
     * Fetches a complete patient record from the active HMS provider and converts
     * it into an HL7 FHIR R4 Bundle.
     *
     * <p><strong>Endpoint:</strong> {@code GET /ehr/patient/{patientId}/bundle}</p>
     * <p><strong>Response:</strong> {@code application/fhir+json} — a FHIR R4 Bundle</p>
     *
     * @param patientId the hospital-assigned patient identifier
     * @return 200 OK with a FHIR Bundle JSON, 404 if patient not found, 500 on error
     */
    @GetMapping(
            value = "/patient/{patientId}/bundle",
            produces = "application/fhir+json"
    )
    public ResponseEntity<String> getPatientBundle(@PathVariable String patientId) {
        PatientRecordDTO record = ehrService.getPatientRecord(patientId);
        String fhirJson = bundleService.convertToFhirBundleJson(record);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("application/fhir+json"))
                .body(fhirJson);
    }
}
