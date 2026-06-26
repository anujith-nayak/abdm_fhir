package in.gov.abdm.fhir.service;

import ca.uhn.fhir.parser.IParser;
import in.gov.abdm.fhir.dto.DiagnosticReportDTO;
import in.gov.abdm.fhir.mapper.DiagnosticReportFhirMapper;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.springframework.stereotype.Service;

/**
 * Service responsible for orchestrating the conversion of a {@link DiagnosticReportDTO}
 * into a pretty-printed HL7 FHIR R4 DiagnosticReport JSON string.
 */
@Service
public class DiagnosticReportFhirService {

    private final DiagnosticReportFhirMapper diagnosticReportFhirMapper;
    private final IParser fhirJsonParser;

    public DiagnosticReportFhirService(DiagnosticReportFhirMapper diagnosticReportFhirMapper,
                                       IParser fhirJsonParser) {
        this.diagnosticReportFhirMapper = diagnosticReportFhirMapper;
        this.fhirJsonParser = fhirJsonParser;
    }

    /**
     * Converts a validated {@link DiagnosticReportDTO} into a FHIR R4 DiagnosticReport JSON string.
     *
     * @param diagnosticReportDTO the validated report data from the hospital system
     * @return a pretty-printed, standards-compliant FHIR R4 DiagnosticReport JSON string
     */
    public String convertToFhirDiagnosticReportJson(DiagnosticReportDTO diagnosticReportDTO) {
        DiagnosticReport fhirReport =
            diagnosticReportFhirMapper.toFhirDiagnosticReport(diagnosticReportDTO);
        return fhirJsonParser.encodeResourceToString(fhirReport);
    }
}
