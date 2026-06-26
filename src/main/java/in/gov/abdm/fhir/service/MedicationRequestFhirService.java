package in.gov.abdm.fhir.service;

import ca.uhn.fhir.parser.IParser;
import in.gov.abdm.fhir.dto.MedicationRequestDTO;
import in.gov.abdm.fhir.mapper.MedicationRequestFhirMapper;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.springframework.stereotype.Service;

/**
 * Service responsible for orchestrating the conversion of a {@link MedicationRequestDTO}
 * into a pretty-printed HL7 FHIR R4 MedicationRequest JSON string.
 */
@Service
public class MedicationRequestFhirService {

    private final MedicationRequestFhirMapper medicationRequestFhirMapper;
    private final IParser fhirJsonParser;

    public MedicationRequestFhirService(MedicationRequestFhirMapper medicationRequestFhirMapper,
                                        IParser fhirJsonParser) {
        this.medicationRequestFhirMapper = medicationRequestFhirMapper;
        this.fhirJsonParser = fhirJsonParser;
    }

    /**
     * Converts a validated {@link MedicationRequestDTO} into a FHIR R4 MedicationRequest JSON string.
     *
     * @param medicationRequestDTO the validated prescription data from the hospital system
     * @return a pretty-printed, standards-compliant FHIR R4 MedicationRequest JSON string
     */
    public String convertToFhirMedicationRequestJson(MedicationRequestDTO medicationRequestDTO) {
        MedicationRequest fhirMedicationRequest =
            medicationRequestFhirMapper.toFhirMedicationRequest(medicationRequestDTO);
        return fhirJsonParser.encodeResourceToString(fhirMedicationRequest);
    }
}
