package in.gov.abdm.fhir.mapper;

import in.gov.abdm.fhir.dto.PractitionerDTO;
import in.gov.abdm.fhir.terminology.FhirConstants;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.stereotype.Component;

/**
 * Maps a {@link PractitionerDTO} received from a hospital system into a HAPI FHIR R4
 * {@link Practitioner} resource.
 *
 * <p>Mapping summary:
 * <ul>
 *   <li>{@code practitionerId} → logical ID + hospital identifier</li>
 *   <li>{@code fullName}       → {@link HumanName} (official use)</li>
 *   <li>{@code specialization} → {@link Practitioner.PractitionerQualificationComponent}</li>
 *   <li>{@code department}     → extension (text annotation via qualification)</li>
 *   <li>{@code phoneNumber}    → {@link ContactPoint} (phone, work)</li>
 *   <li>{@code email}          → {@link ContactPoint} (email, work)</li>
 * </ul>
 * </p>
 *
 * <p>Note: {@code specialization} and {@code department} are expressed via the
 * {@code Practitioner.qualification} field using a plain {@link CodeableConcept} text,
 * which is the correct FHIR R4 approach when a formal coding system is not mandated.</p>
 */
@Component
public class PractitionerFhirMapper {

    /** System URI for hospital-assigned practitioner identifiers. */
    private static final String HOSPITAL_PRACTITIONER_SYSTEM = FhirConstants.HOSPITAL_PRACTITIONER_SYSTEM;

    /**
     * Converts a validated {@link PractitionerDTO} into a FHIR R4 {@link Practitioner} resource.
     *
     * @param dto the input practitioner data; must not be null
     * @return a fully populated HAPI FHIR {@link Practitioner} resource
     */
    public Practitioner toFhirPractitioner(PractitionerDTO dto) {
        Practitioner practitioner = new Practitioner();

        // --- Logical ID ---
        practitioner.setId(dto.getPractitionerId());

        // --- Hospital identifier ---
        practitioner.addIdentifier(
            new Identifier()
                .setSystem(HOSPITAL_PRACTITIONER_SYSTEM)
                .setValue(dto.getPractitionerId())
                .setUse(Identifier.IdentifierUse.USUAL)
        );

        // --- Full name ---
        practitioner.addName(
            new HumanName()
                .setUse(HumanName.NameUse.OFFICIAL)
                .setText(dto.getFullName())
        );

        // --- Specialization (FHIR qualification) ---
        if (dto.getSpecialization() != null && !dto.getSpecialization().isBlank()) {
            Practitioner.PractitionerQualificationComponent qualification =
                new Practitioner.PractitionerQualificationComponent();
            qualification.setCode(
                new CodeableConcept().setText(dto.getSpecialization())
            );
            practitioner.addQualification(qualification);
        }

        // --- Department (second qualification entry with "department" text prefix) ---
        if (dto.getDepartment() != null && !dto.getDepartment().isBlank()) {
            Practitioner.PractitionerQualificationComponent deptQualification =
                new Practitioner.PractitionerQualificationComponent();
            deptQualification.setCode(
                new CodeableConcept().setText("Department: " + dto.getDepartment())
            );
            practitioner.addQualification(deptQualification);
        }

        // --- Phone (optional) ---
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isBlank()) {
            practitioner.addTelecom(
                new ContactPoint()
                    .setSystem(ContactPoint.ContactPointSystem.PHONE)
                    .setValue(dto.getPhoneNumber())
                    .setUse(ContactPoint.ContactPointUse.WORK)
            );
        }

        // --- Email (optional) ---
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            practitioner.addTelecom(
                new ContactPoint()
                    .setSystem(ContactPoint.ContactPointSystem.EMAIL)
                    .setValue(dto.getEmail())
                    .setUse(ContactPoint.ContactPointUse.WORK)
            );
        }

        return practitioner;
    }
}
