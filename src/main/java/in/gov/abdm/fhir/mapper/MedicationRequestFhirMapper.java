package in.gov.abdm.fhir.mapper;

import in.gov.abdm.fhir.dto.MedicationRequestDTO;
import in.gov.abdm.fhir.terminology.FhirConstants;
import in.gov.abdm.fhir.terminology.MedicationCodes;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Dosage;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.stereotype.Component;

/**
 * Maps a {@link MedicationRequestDTO} received from a hospital system into a HAPI FHIR R4
 * {@link MedicationRequest} resource.
 *
 * <p>Medication coding uses {@link MedicationCodes} from the terminology package.
 * Currently the service uses text-only {@link CodeableConcept} entries because ABDM
 * has not yet published a mandatory medication coding system. The architecture is
 * deliberately designed so that switching to RxNorm, SNOMED CT, or the ABDM
 * medication system requires a one-line change in {@link MedicationCodes} only.</p>
 *
 * <p>All system URI constants are sourced from {@link FhirConstants}.</p>
 *
 * <p>Mapping summary:
 * <ul>
 *   <li>{@code prescriptionId} → logical ID + hospital identifier</li>
 *   <li>{@code patientId}      → subject reference ({@code Patient/<id>})</li>
 *   <li>{@code medicineName}   → medication[x] via {@link MedicationCodes#buildTextOnlyConcept}</li>
 *   <li>{@code dosage}         → {@link Dosage} text sig + type</li>
 *   <li>{@code frequency}      → included in human-readable sig</li>
 *   <li>{@code duration}       → included in human-readable sig</li>
 *   <li>{@code instructions}   → patient instruction + {@link Annotation} note</li>
 * </ul>
 * </p>
 */
@Component
public class MedicationRequestFhirMapper {

    /**
     * Converts a validated {@link MedicationRequestDTO} into a FHIR R4 {@link MedicationRequest}.
     *
     * @param dto the input prescription data; must not be null
     * @return a fully populated HAPI FHIR {@link MedicationRequest} resource
     */
    public MedicationRequest toFhirMedicationRequest(MedicationRequestDTO dto) {
        MedicationRequest medicationRequest = new MedicationRequest();

        // --- Logical ID ---
        medicationRequest.setId(dto.getPrescriptionId());

        // --- Identifier ---
        medicationRequest.addIdentifier(
            new Identifier()
                .setSystem(FhirConstants.HOSPITAL_PRESCRIPTION_SYSTEM)
                .setValue(dto.getPrescriptionId())
                .setUse(Identifier.IdentifierUse.USUAL)
        );

        // --- Status: active for all new prescriptions ---
        medicationRequest.setStatus(MedicationRequest.MedicationRequestStatus.ACTIVE);

        // --- Intent: order (clinician-issued prescription) ---
        medicationRequest.setIntent(MedicationRequest.MedicationRequestIntent.ORDER);

        // --- Medication concept — sourced from MedicationCodes terminology class.
        //     Text-only is valid FHIR R4 and the current ABDM-approved approach.
        //     Replace with MedicationCodes.buildAbdmCodedConcept() once codes are published.
        medicationRequest.setMedication(
            MedicationCodes.buildTextOnlyConcept(dto.getMedicineName())
        );

        // --- Subject (patient reference) ---
        medicationRequest.setSubject(
            new Reference(FhirConstants.PATIENT_REF_PREFIX + dto.getPatientId())
        );

        // --- Dosage instruction ---
        medicationRequest.addDosageInstruction(buildDosage(dto));

        // --- Patient instructions note (optional) ---
        if (dto.getInstructions() != null && !dto.getInstructions().isBlank()) {
            medicationRequest.addNote(
                new Annotation().setText(dto.getInstructions())
            );
        }

        return medicationRequest;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Builds a human-readable {@link Dosage} sig from the DTO fields.
     *
     * <p>Example: {@code "500 mg — twice daily — for 5 days"}</p>
     */
    private Dosage buildDosage(MedicationRequestDTO dto) {
        String sig = dto.getDosage() + " — " + dto.getFrequency() + " — for " + dto.getDuration();

        Dosage dosage = new Dosage();
        dosage.setText(sig);

        // Encode dosage type as CodeableConcept text (dose[x] only accepts Quantity|Range)
        Dosage.DosageDoseAndRateComponent doseAndRate = new Dosage.DosageDoseAndRateComponent();
        doseAndRate.setType(new CodeableConcept().setText(dto.getDosage()));
        dosage.addDoseAndRate(doseAndRate);

        // Patient-facing instructions
        if (dto.getInstructions() != null && !dto.getInstructions().isBlank()) {
            dosage.setPatientInstruction(dto.getInstructions());
        }

        return dosage;
    }
}
