package in.gov.abdm.fhir.mapper;

import in.gov.abdm.fhir.dto.MedicationRequestDTO;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Dosage;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;

/**
 * Maps a {@link MedicationRequestDTO} received from a hospital system into a HAPI FHIR R4
 * {@link MedicationRequest} resource.
 *
 * <p>Mapping summary:
 * <ul>
 *   <li>{@code prescriptionId} → logical ID + hospital identifier</li>
 *   <li>{@code patientId}      → subject reference ({@code Patient/<id>})</li>
 *   <li>{@code medicineName}   → medication[x] as {@link CodeableConcept} text</li>
 *   <li>{@code dosage}         → {@link Dosage#getDoseAndRate()} type text</li>
 *   <li>{@code frequency}      → {@link Dosage#getText()} (human-readable sig)</li>
 *   <li>{@code duration}       → {@link Dosage#getText()} combined with frequency</li>
 *   <li>{@code instructions}   → {@link Annotation} (patient note)</li>
 * </ul>
 * </p>
 */
@Component
public class MedicationRequestFhirMapper {

    private static final String HOSPITAL_RX_SYSTEM = "https://hospital.example.org/prescriptions";
    private static final String PATIENT_REFERENCE_PREFIX = "Patient/";

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
                .setSystem(HOSPITAL_RX_SYSTEM)
                .setValue(dto.getPrescriptionId())
                .setUse(Identifier.IdentifierUse.USUAL)
        );

        // --- Status: active for all new prescriptions ---
        medicationRequest.setStatus(MedicationRequest.MedicationRequestStatus.ACTIVE);

        // --- Intent: order (clinician-issued prescription) ---
        medicationRequest.setIntent(MedicationRequest.MedicationRequestIntent.ORDER);

        // --- Medication as CodeableConcept text (no formal coding required) ---
        medicationRequest.setMedication(
            new CodeableConcept().setText(dto.getMedicineName())
        );

        // --- Subject (patient reference) ---
        medicationRequest.setSubject(
            new Reference(PATIENT_REFERENCE_PREFIX + dto.getPatientId())
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
     * Builds a human-readable {@link Dosage} from the DTO fields.
     *
     * <p>Example sig: {@code "500 mg — twice daily — for 5 days"}</p>
     */
    private Dosage buildDosage(MedicationRequestDTO dto) {
        String sig = dto.getDosage() + " — " + dto.getFrequency() + " — for " + dto.getDuration();

        Dosage dosage = new Dosage();
        dosage.setText(sig);

        // Encode dosage amount as dose-and-rate type text (dose[x] accepts Quantity|Range only)
        Dosage.DosageDoseAndRateComponent doseAndRate = new Dosage.DosageDoseAndRateComponent();
        doseAndRate.setType(new CodeableConcept().setText(dto.getDosage()));
        dosage.addDoseAndRate(doseAndRate);

        // Additional instructions as patient instructions field
        if (dto.getInstructions() != null && !dto.getInstructions().isBlank()) {
            dosage.setPatientInstruction(dto.getInstructions());
        }

        return dosage;
    }
}
