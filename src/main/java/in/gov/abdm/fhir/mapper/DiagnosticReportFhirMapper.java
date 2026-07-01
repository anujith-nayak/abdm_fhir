package in.gov.abdm.fhir.mapper;

import in.gov.abdm.fhir.dto.DiagnosticReportDTO;
import in.gov.abdm.fhir.exception.InvalidDateException;
import in.gov.abdm.fhir.terminology.DiagnosticReportCodes;
import in.gov.abdm.fhir.terminology.FhirConstants;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Maps a {@link DiagnosticReportDTO} received from a hospital system into a HAPI FHIR R4
 * {@link DiagnosticReport} resource.
 *
 * <p>All coded concepts (category, report type) are sourced from the centralised
 * terminology package ({@link DiagnosticReportCodes}, {@link FhirConstants}).</p>
 *
 * <p>Category is inferred from the report name keyword
 * ({@link DiagnosticReportCodes#inferCategory}) — e.g. "MRI" → Radiology,
 * "CBC" → Laboratory. Formal LOINC codes for the report type will be wired in
 * Phase 4 via {@link DiagnosticReportCodes#buildCodedReportType}.</p>
 *
 * <p>Mapping summary:
 * <ul>
 *   <li>{@code reportId}     → logical ID + hospital identifier</li>
 *   <li>{@code patientId}    → subject reference ({@code Patient/<id>})</li>
 *   <li>{@code reportName}   → text-only code concept + inferred category</li>
 *   <li>{@code reportStatus} → {@link DiagnosticReport.DiagnosticReportStatus} enum</li>
 *   <li>{@code conclusion}   → conclusion string</li>
 *   <li>{@code reportDate}   → effective[x] as {@link DateTimeType}</li>
 * </ul>
 * </p>
 */
@Component
public class DiagnosticReportFhirMapper {

    /**
     * Converts a validated {@link DiagnosticReportDTO} into a FHIR R4 {@link DiagnosticReport}.
     *
     * @param dto the input report data; must not be null
     * @return a fully populated HAPI FHIR {@link DiagnosticReport} resource
     * @throws InvalidDateException if {@code reportDate} is not a valid calendar date
     */
    public DiagnosticReport toFhirDiagnosticReport(DiagnosticReportDTO dto) {
        DiagnosticReport report = new DiagnosticReport();

        // --- Logical ID ---
        report.setId(dto.getReportId());

        // --- Identifier ---
        report.addIdentifier(
            new Identifier()
                .setSystem(FhirConstants.HOSPITAL_REPORT_SYSTEM)
                .setValue(dto.getReportId())
                .setUse(Identifier.IdentifierUse.USUAL)
        );

        // --- Status ---
        report.setStatus(resolveStatus(dto.getReportStatus()));

        // --- Category — keyword-inferred using DiagnosticReportCodes.
        //     Replace inferCategory() with a lookup when ABDM category codes are published.
        report.addCategory(DiagnosticReportCodes.inferCategory(dto.getReportName()));

        // --- Code (report type) — text-only for now; switch to buildCodedReportType()
        //     once LOINC document codes are mapped.
        report.setCode(DiagnosticReportCodes.buildTextOnlyReportType(dto.getReportName()));

        // --- Subject (patient reference) ---
        report.setSubject(
            new Reference(FhirConstants.PATIENT_REF_PREFIX + dto.getPatientId())
        );

        // --- Effective date ---
        report.setEffective(parseReportDate(dto.getReportDate()));

        // --- Conclusion (optional) ---
        if (dto.getConclusion() != null && !dto.getConclusion().isBlank()) {
            report.setConclusion(dto.getConclusion());
        }

        return report;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Resolves the DTO status string to the FHIR
     * {@link DiagnosticReport.DiagnosticReportStatus} enum.
     */
    private DiagnosticReport.DiagnosticReportStatus resolveStatus(String status) {
        return switch (status.toLowerCase()) {
            case "registered"       -> DiagnosticReport.DiagnosticReportStatus.REGISTERED;
            case "partial"          -> DiagnosticReport.DiagnosticReportStatus.PARTIAL;
            case "preliminary"      -> DiagnosticReport.DiagnosticReportStatus.PRELIMINARY;
            case "final"            -> DiagnosticReport.DiagnosticReportStatus.FINAL;
            case "amended"          -> DiagnosticReport.DiagnosticReportStatus.AMENDED;
            case "corrected"        -> DiagnosticReport.DiagnosticReportStatus.CORRECTED;
            case "appended"         -> DiagnosticReport.DiagnosticReportStatus.APPENDED;
            case "cancelled"        -> DiagnosticReport.DiagnosticReportStatus.CANCELLED;
            case "entered-in-error" -> DiagnosticReport.DiagnosticReportStatus.ENTEREDINERROR;
            default                 -> DiagnosticReport.DiagnosticReportStatus.UNKNOWN;
        };
    }

    /**
     * Parses an ISO-8601 date string into a FHIR {@link DateTimeType}.
     *
     * @throws InvalidDateException if the date is not a valid calendar date
     */
    private DateTimeType parseReportDate(String reportDate) {
        try {
            LocalDate.parse(reportDate);
            return new DateTimeType(reportDate);
        } catch (DateTimeParseException ex) {
            throw new InvalidDateException(
                "Invalid reportDate value: '" + reportDate + "'. Expected yyyy-MM-dd.");
        }
    }
}
