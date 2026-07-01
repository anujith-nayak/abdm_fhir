package in.gov.abdm.fhir.ehr.dto;

/**
 * Generic EHR DTO representing a single patient visit / encounter captured
 * by a Hospital Management System.
 *
 * <p>This is a plain data carrier with no FHIR semantics. The EHR mapper layer
 * is responsible for transforming it into existing FHIR DTOs.</p>
 */
public class VisitDTO {

    /** Hospital-assigned visit / encounter identifier. */
    private String visitId;

    /** ID of the patient this visit belongs to. */
    private String patientId;

    /** ID of the attending practitioner. */
    private String practitionerId;

    /** Date of the visit in yyyy-MM-dd format. */
    private String visitDate;

    /** Type of visit (e.g. OPD, IPD, Emergency). */
    private String visitType;

    /** Department where the visit occurred. */
    private String department;

    /** Free-text clinical notes recorded during the visit. */
    private String notes;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public VisitDTO() {}

    public VisitDTO(String visitId, String patientId, String practitionerId,
                    String visitDate, String visitType, String department, String notes) {
        this.visitId = visitId;
        this.patientId = patientId;
        this.practitionerId = practitionerId;
        this.visitDate = visitDate;
        this.visitType = visitType;
        this.department = department;
        this.notes = notes;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public String getVisitId() { return visitId; }
    public void setVisitId(String visitId) { this.visitId = visitId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getPractitionerId() { return practitionerId; }
    public void setPractitionerId(String practitionerId) { this.practitionerId = practitionerId; }

    public String getVisitDate() { return visitDate; }
    public void setVisitDate(String visitDate) { this.visitDate = visitDate; }

    public String getVisitType() { return visitType; }
    public void setVisitType(String visitType) { this.visitType = visitType; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String visitId;
        private String patientId;
        private String practitionerId;
        private String visitDate;
        private String visitType;
        private String department;
        private String notes;

        private Builder() {}

        public Builder visitId(String visitId) { this.visitId = visitId; return this; }
        public Builder patientId(String patientId) { this.patientId = patientId; return this; }
        public Builder practitionerId(String practitionerId) { this.practitionerId = practitionerId; return this; }
        public Builder visitDate(String visitDate) { this.visitDate = visitDate; return this; }
        public Builder visitType(String visitType) { this.visitType = visitType; return this; }
        public Builder department(String department) { this.department = department; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }

        public VisitDTO build() {
            return new VisitDTO(visitId, patientId, practitionerId,
                                visitDate, visitType, department, notes);
        }
    }
}
