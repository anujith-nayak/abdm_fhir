package in.gov.abdm.fhir.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object representing a complete hospital record.
 *
 * <p>Used as the input contract for {@code POST /fhir/bundle}. Every section is
 * converted into its corresponding FHIR resource and assembled into a single
 * HL7 FHIR R4 Bundle.</p>
 */
public class PatientRecordDTO {

    /** Patient demographics — mandatory. */
    @NotNull(message = "Patient information is mandatory")
    @Valid
    private PatientDTO patient;

    /** Attending practitioner — mandatory. */
    @NotNull(message = "Practitioner information is mandatory")
    @Valid
    private PractitionerDTO practitioner;

    /** Clinical observations for the patient. May be empty. */
    @Valid
    private List<ObservationDTO> observations = new ArrayList<>();

    /** Medication prescriptions for the patient. May be empty. */
    @Valid
    private List<MedicationRequestDTO> medications = new ArrayList<>();

    /** Diagnostic reports for the patient. May be empty. */
    @Valid
    private List<DiagnosticReportDTO> diagnosticReports = new ArrayList<>();

    // -------------------------------------------------------------------------
    // No-arg constructor (required by Jackson)
    // -------------------------------------------------------------------------

    public PatientRecordDTO() {}

    // -------------------------------------------------------------------------
    // All-args constructor
    // -------------------------------------------------------------------------

    public PatientRecordDTO(PatientDTO patient,
                            PractitionerDTO practitioner,
                            List<ObservationDTO> observations,
                            List<MedicationRequestDTO> medications,
                            List<DiagnosticReportDTO> diagnosticReports) {
        this.patient = patient;
        this.practitioner = practitioner;
        this.observations = observations != null ? observations : new ArrayList<>();
        this.medications = medications != null ? medications : new ArrayList<>();
        this.diagnosticReports = diagnosticReports != null ? diagnosticReports : new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public PatientDTO getPatient() { return patient; }
    public void setPatient(PatientDTO patient) { this.patient = patient; }

    public PractitionerDTO getPractitioner() { return practitioner; }
    public void setPractitioner(PractitionerDTO practitioner) { this.practitioner = practitioner; }

    public List<ObservationDTO> getObservations() { return observations; }
    public void setObservations(List<ObservationDTO> observations) {
        this.observations = observations != null ? observations : new ArrayList<>();
    }

    public List<MedicationRequestDTO> getMedications() { return medications; }
    public void setMedications(List<MedicationRequestDTO> medications) {
        this.medications = medications != null ? medications : new ArrayList<>();
    }

    public List<DiagnosticReportDTO> getDiagnosticReports() { return diagnosticReports; }
    public void setDiagnosticReports(List<DiagnosticReportDTO> diagnosticReports) {
        this.diagnosticReports = diagnosticReports != null ? diagnosticReports : new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private PatientDTO patient;
        private PractitionerDTO practitioner;
        private List<ObservationDTO> observations = new ArrayList<>();
        private List<MedicationRequestDTO> medications = new ArrayList<>();
        private List<DiagnosticReportDTO> diagnosticReports = new ArrayList<>();

        private Builder() {}

        public Builder patient(PatientDTO patient) { this.patient = patient; return this; }
        public Builder practitioner(PractitionerDTO practitioner) { this.practitioner = practitioner; return this; }
        public Builder observations(List<ObservationDTO> observations) {
            this.observations = observations != null ? observations : new ArrayList<>();
            return this;
        }
        public Builder medications(List<MedicationRequestDTO> medications) {
            this.medications = medications != null ? medications : new ArrayList<>();
            return this;
        }
        public Builder diagnosticReports(List<DiagnosticReportDTO> diagnosticReports) {
            this.diagnosticReports = diagnosticReports != null ? diagnosticReports : new ArrayList<>();
            return this;
        }

        public PatientRecordDTO build() {
            return new PatientRecordDTO(patient, practitioner, observations, medications, diagnosticReports);
        }
    }

    @Override
    public String toString() {
        return "PatientRecordDTO{patient=" + patient + ", practitioner=" + practitioner + "}";
    }
}
