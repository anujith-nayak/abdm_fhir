package in.gov.abdm.fhir.service;

import ca.uhn.fhir.parser.IParser;
import in.gov.abdm.fhir.dto.ObservationDTO;
import in.gov.abdm.fhir.mapper.ObservationFhirMapper;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.stereotype.Service;

/**
 * Service responsible for orchestrating the conversion of an {@link ObservationDTO}
 * into a pretty-printed HL7 FHIR R4 Observation JSON string.
 */
@Service
public class ObservationFhirService {

    private final ObservationFhirMapper observationFhirMapper;
    private final IParser fhirJsonParser;

    public ObservationFhirService(ObservationFhirMapper observationFhirMapper,
                                  IParser fhirJsonParser) {
        this.observationFhirMapper = observationFhirMapper;
        this.fhirJsonParser = fhirJsonParser;
    }

    /**
     * Converts a validated {@link ObservationDTO} into a FHIR R4 Observation JSON string.
     *
     * @param observationDTO the validated observation data from the hospital system
     * @return a pretty-printed, standards-compliant FHIR R4 Observation JSON string
     */
    public String convertToFhirObservationJson(ObservationDTO observationDTO) {
        Observation fhirObservation = observationFhirMapper.toFhirObservation(observationDTO);
        return fhirJsonParser.encodeResourceToString(fhirObservation);
    }
}
