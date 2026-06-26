package in.gov.abdm.fhir.service;

import ca.uhn.fhir.parser.IParser;
import in.gov.abdm.fhir.dto.PatientDTO;
import in.gov.abdm.fhir.mapper.PatientFhirMapper;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;

/**
 * Service responsible for orchestrating the conversion of a {@link PatientDTO}
 * into a pretty-printed HL7 FHIR R4 Patient JSON string.
 *
 * <p>Workflow:
 * <ol>
 *   <li>Receive a validated {@link PatientDTO}</li>
 *   <li>Delegate mapping to {@link PatientFhirMapper}</li>
 *   <li>Serialise the resulting {@link Patient} resource using HAPI FHIR's {@link IParser}</li>
 *   <li>Return the JSON string to the controller</li>
 * </ol>
 * </p>
 */
@Service
public class PatientFhirService {

    private final PatientFhirMapper patientFhirMapper;
    private final IParser fhirJsonParser;

    /**
     * Constructs the service with its required dependencies via constructor injection.
     *
     * @param patientFhirMapper the mapper that converts DTOs to FHIR resources
     * @param fhirJsonParser    the HAPI FHIR JSON parser configured for pretty-print output
     */
    public PatientFhirService(PatientFhirMapper patientFhirMapper, IParser fhirJsonParser) {
        this.patientFhirMapper = patientFhirMapper;
        this.fhirJsonParser = fhirJsonParser;
    }

    /**
     * Converts a validated {@link PatientDTO} into a FHIR R4 Patient JSON string.
     *
     * @param patientDTO the validated patient data from the hospital system
     * @return a pretty-printed, standards-compliant FHIR R4 Patient JSON string
     */
    public String convertToFhirPatientJson(PatientDTO patientDTO) {
        Patient fhirPatient = patientFhirMapper.toFhirPatient(patientDTO);
        return fhirJsonParser.encodeResourceToString(fhirPatient);
    }
}
