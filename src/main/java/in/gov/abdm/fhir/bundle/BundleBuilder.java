package in.gov.abdm.fhir.bundle;

import in.gov.abdm.fhir.dto.PatientRecordDTO;
import in.gov.abdm.fhir.mapper.DiagnosticReportFhirMapper;
import in.gov.abdm.fhir.mapper.MedicationRequestFhirMapper;
import in.gov.abdm.fhir.mapper.ObservationFhirMapper;
import in.gov.abdm.fhir.mapper.PatientFhirMapper;
import in.gov.abdm.fhir.mapper.PractitionerFhirMapper;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Assembles a FHIR R4 {@link Bundle} from a complete {@link PatientRecordDTO}.
 *
 * <p>Delegates resource mapping to the existing FHIR mappers and wires cross-resource
 * references using stable logical IDs from the patient and practitioner sections.</p>
 */
@Component
public class BundleBuilder {

    private static final String PATIENT_REFERENCE_PREFIX = "Patient/";
    private static final String PRACTITIONER_REFERENCE_PREFIX = "Practitioner/";

    private final PatientFhirMapper patientFhirMapper;
    private final PractitionerFhirMapper practitionerFhirMapper;
    private final ObservationFhirMapper observationFhirMapper;
    private final MedicationRequestFhirMapper medicationRequestFhirMapper;
    private final DiagnosticReportFhirMapper diagnosticReportFhirMapper;

    public BundleBuilder(PatientFhirMapper patientFhirMapper,
                         PractitionerFhirMapper practitionerFhirMapper,
                         ObservationFhirMapper observationFhirMapper,
                         MedicationRequestFhirMapper medicationRequestFhirMapper,
                         DiagnosticReportFhirMapper diagnosticReportFhirMapper) {
        this.patientFhirMapper = patientFhirMapper;
        this.practitionerFhirMapper = practitionerFhirMapper;
        this.observationFhirMapper = observationFhirMapper;
        this.medicationRequestFhirMapper = medicationRequestFhirMapper;
        this.diagnosticReportFhirMapper = diagnosticReportFhirMapper;
    }

    /**
     * Converts every section of the hospital record into FHIR resources and
     * assembles them into a collection Bundle.
     *
     * @param record the validated hospital record; must not be null
     * @return a populated HAPI FHIR R4 {@link Bundle}
     * @throws IllegalArgumentException if the resulting bundle would contain no entries
     */
    public Bundle buildBundle(PatientRecordDTO record) {
        Patient patient = patientFhirMapper.toFhirPatient(record.getPatient());
        Practitioner practitioner = practitionerFhirMapper.toFhirPractitioner(record.getPractitioner());

        String patientReference = PATIENT_REFERENCE_PREFIX + patient.getIdElement().getIdPart();
        String practitionerReference = PRACTITIONER_REFERENCE_PREFIX + practitioner.getIdElement().getIdPart();

        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.COLLECTION);

        addEntry(bundle, patient);
        addEntry(bundle, practitioner);

        for (var observationDto : nullSafe(record.getObservations())) {
            Observation observation = observationFhirMapper.toFhirObservation(observationDto);
            observation.setSubject(new Reference(patientReference));
            addEntry(bundle, observation);
        }

        for (var medicationDto : nullSafe(record.getMedications())) {
            MedicationRequest medicationRequest = medicationRequestFhirMapper.toFhirMedicationRequest(medicationDto);
            medicationRequest.setSubject(new Reference(patientReference));
            medicationRequest.setRequester(new Reference(practitionerReference));
            addEntry(bundle, medicationRequest);
        }

        for (var reportDto : nullSafe(record.getDiagnosticReports())) {
            DiagnosticReport report = diagnosticReportFhirMapper.toFhirDiagnosticReport(reportDto);
            report.setSubject(new Reference(patientReference));
            report.addPerformer(new Reference(practitionerReference));
            addEntry(bundle, report);
        }

        if (bundle.getEntry().isEmpty()) {
            throw new IllegalArgumentException("Bundle cannot be empty");
        }

        return bundle;
    }

    private void addEntry(Bundle bundle, Resource resource) {
        Bundle.BundleEntryComponent entry = bundle.addEntry();
        entry.setFullUrl(resource.getResourceType().name() + "/" + resource.getIdElement().getIdPart());
        entry.setResource(resource);
    }

    private <T> List<T> nullSafe(List<T> list) {
        return list != null ? list : List.of();
    }
}
