package in.gov.abdm.fhir.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object representing a diagnostic report received from a hospital system.
 *
 * <p>Used as the input contract for {@code POST /fhir/diagnostic-report}.</p>
 */
public class DiagnosticReportDTO {

    /** Hospital-assigned report identifier. Must not be blank. */
    @NotBlank(message = "Report ID must not be blank")
    private String reportId;

    /** Reference to the patient the report belongs to. Must not be blank. */
    @NotBlank(message = "Patient ID must not be blank")
    private String patientId;

    /** Human-readable name / title of the report (e.g. "Complete Blood Count"). Must not be blank. */
    @NotBlank(message = "Report name must not be blank")
    private String reportName;

    /**
     * Status of the report.
     * Must be one of FHIR's DiagnosticReport status codes:
     * registered, partial, preliminary, final, amended, corrected, appended, cancelled, entered-in-error, unknown.
     */
    @NotBlank(message = "Report status must not be blank")
    @Pattern(
        regexp = "(?i)^(registered|partial|preliminary|final|amended|corrected|appended|cancelled|entered-in-error|unknown)$",
        message = "Report status must be one of: registered, partial, preliminary, final, amended, corrected, appended, cancelled, entered-in-error, unknown"
    )
    private String reportStatus;

    /** Clinical conclusion or interpretation of the report. Optional. */
    private String conclusion;

    /**
     * Date the report was issued, in yyyy-MM-dd format. Must not be blank.
     */
    @NotBlank(message = "Report date must not be blank")
    @Pattern(
        regexp = "^\\d{4}-\\d{2}-\\d{2}$",
        message = "Report date must be in yyyy-MM-dd format"
    )
    private String reportDate;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DiagnosticReportDTO() {}

    public DiagnosticReportDTO(String reportId, String patientId, String reportName,
                               String reportStatus, String conclusion, String reportDate) {
        this.reportId = reportId;
        this.patientId = patientId;
        this.reportName = reportName;
        this.reportStatus = reportStatus;
        this.conclusion = conclusion;
        this.reportDate = reportDate;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getReportName() { return reportName; }
    public void setReportName(String reportName) { this.reportName = reportName; }

    public String getReportStatus() { return reportStatus; }
    public void setReportStatus(String reportStatus) { this.reportStatus = reportStatus; }

    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }

    public String getReportDate() { return reportDate; }
    public void setReportDate(String reportDate) { this.reportDate = reportDate; }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String reportId;
        private String patientId;
        private String reportName;
        private String reportStatus;
        private String conclusion;
        private String reportDate;

        private Builder() {}

        public Builder reportId(String reportId) { this.reportId = reportId; return this; }
        public Builder patientId(String patientId) { this.patientId = patientId; return this; }
        public Builder reportName(String reportName) { this.reportName = reportName; return this; }
        public Builder reportStatus(String reportStatus) { this.reportStatus = reportStatus; return this; }
        public Builder conclusion(String conclusion) { this.conclusion = conclusion; return this; }
        public Builder reportDate(String reportDate) { this.reportDate = reportDate; return this; }

        public DiagnosticReportDTO build() {
            return new DiagnosticReportDTO(reportId, patientId, reportName,
                                           reportStatus, conclusion, reportDate);
        }
    }

    @Override
    public String toString() {
        return "DiagnosticReportDTO{reportId='" + reportId + "', patientId='" + patientId +
               "', reportName='" + reportName + "'}";
    }
}
