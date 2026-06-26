package in.gov.abdm.fhir.bundle;

import ca.uhn.fhir.parser.IParser;
import in.gov.abdm.fhir.dto.PatientRecordDTO;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.stereotype.Service;

/**
 * Service responsible for orchestrating the conversion of a {@link PatientRecordDTO}
 * into a pretty-printed HL7 FHIR R4 Bundle JSON string.
 */
@Service
public class BundleService {

    private final BundleBuilder bundleBuilder;
    private final IParser fhirJsonParser;

    public BundleService(BundleBuilder bundleBuilder, IParser fhirJsonParser) {
        this.bundleBuilder = bundleBuilder;
        this.fhirJsonParser = fhirJsonParser;
    }

    /**
     * Converts a validated hospital record into a FHIR R4 Bundle JSON string.
     *
     * @param record the validated hospital record
     * @return a pretty-printed, standards-compliant FHIR R4 Bundle JSON string
     */
    public String convertToFhirBundleJson(PatientRecordDTO record) {
        Bundle bundle = bundleBuilder.buildBundle(record);
        return fhirJsonParser.encodeResourceToString(bundle);
    }
}
