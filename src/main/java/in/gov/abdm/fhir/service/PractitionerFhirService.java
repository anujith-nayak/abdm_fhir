package in.gov.abdm.fhir.service;

import ca.uhn.fhir.parser.IParser;
import in.gov.abdm.fhir.dto.PractitionerDTO;
import in.gov.abdm.fhir.mapper.PractitionerFhirMapper;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.stereotype.Service;

/**
 * Service responsible for orchestrating the conversion of a {@link PractitionerDTO}
 * into a pretty-printed HL7 FHIR R4 Practitioner JSON string.
 */
@Service
public class PractitionerFhirService {

    private final PractitionerFhirMapper practitionerFhirMapper;
    private final IParser fhirJsonParser;

    public PractitionerFhirService(PractitionerFhirMapper practitionerFhirMapper,
                                   IParser fhirJsonParser) {
        this.practitionerFhirMapper = practitionerFhirMapper;
        this.fhirJsonParser = fhirJsonParser;
    }

    /**
     * Converts a validated {@link PractitionerDTO} into a FHIR R4 Practitioner JSON string.
     *
     * @param practitionerDTO the validated practitioner data from the hospital system
     * @return a pretty-printed, standards-compliant FHIR R4 Practitioner JSON string
     */
    public String convertToFhirPractitionerJson(PractitionerDTO practitionerDTO) {
        Practitioner fhirPractitioner = practitionerFhirMapper.toFhirPractitioner(practitionerDTO);
        return fhirJsonParser.encodeResourceToString(fhirPractitioner);
    }
}
