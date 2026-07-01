package in.gov.abdm.fhir.ehr.dto;

import java.util.List;

/**
 * Generic EHR DTO representing a doctor visit — a {@link VisitDTO} enriched with
 * the attending doctor's details and the clinical findings recorded during the visit.
 *
 * <p>This DTO aggregates the visit context with practitioner-level detail so that
 * a single HMS record can be decomposed into both a FHIR Practitioner and a
 * FHIR Observation (or Encounter) by the mapper layer.</p>
 */
public class DoctorVisitDTO {

    /** The visit context (date, department, type). */
    private VisitDTO visit;

    /** Doctor identifier — maps to {@code PractitionerDTO.practitionerId}. */
    private String doctorId;

    /** Doctor full name — maps to {@code PractitionerDTO.fullName}. */
    private String doctorName;

    /** Doctor specialization — maps to {@code PractitionerDTO.specialization}. */
    private String specialization;

    /** Chief complaint presented by the patient. */
    private String chiefComplaint;

    /** Clinical diagnosis text. */
    private String diagnosis;

    /** Vital sign observations recorded during the visit. */
    private List<LabResultDTO> vitals;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DoctorVisitDTO() {}

    public DoctorVisitDTO(VisitDTO visit, String doctorId, String doctorName,
                          String specialization, String chiefComplaint,
                          String diagnosis, List<LabResultDTO> vitals) {
        this.visit = visit;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.chiefComplaint = chiefComplaint;
        this.diagnosis = diagnosis;
        this.vitals = vitals;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public VisitDTO getVisit() { return visit; }
    public void setVisit(VisitDTO visit) { this.visit = visit; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getChiefComplaint() { return chiefComplaint; }
    public void setChiefComplaint(String chiefComplaint) { this.chiefComplaint = chiefComplaint; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public List<LabResultDTO> getVitals() { return vitals; }
    public void setVitals(List<LabResultDTO> vitals) { this.vitals = vitals; }
}
