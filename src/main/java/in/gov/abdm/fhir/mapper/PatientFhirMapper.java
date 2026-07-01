package in.gov.abdm.fhir.mapper;

import in.gov.abdm.fhir.dto.PatientDTO;
import in.gov.abdm.fhir.exception.InvalidDateException;
import in.gov.abdm.fhir.terminology.FhirConstants;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Maps a {@link PatientDTO} received from a hospital system into a HAPI FHIR R4
 * {@link Patient} resource.
 *
 * <p>No manual JSON construction is performed here. All mapping uses the HAPI FHIR
 * model API directly, ensuring the resulting resource is fully FHIR-compliant.</p>
 *
 * <p>Mapping summary:
 * <ul>
 *   <li>{@code patientId}  → {@link Patient} logical ID + hospital identifier</li>
 *   <li>{@code fullName}   → {@link HumanName} (official use)</li>
 *   <li>{@code gender}     → {@link AdministrativeGender}</li>
 *   <li>{@code dateOfBirth}→ {@link DateType}</li>
 *   <li>{@code phoneNumber}→ {@link ContactPoint} (phone, home)</li>
 *   <li>{@code address}    → {@link Address} (home)</li>
 *   <li>{@code abhaNumber} → {@link Identifier} with ABHA system URI</li>
 * </ul>
 * </p>
 */
@Component
public class PatientFhirMapper {

    /** System URI for hospital-assigned patient identifiers. */
    private static final String HOSPITAL_PATIENT_SYSTEM = FhirConstants.HOSPITAL_PATIENT_SYSTEM;

    /** System URI for ABHA (Ayushman Bharat Health Account) identifiers. */
    private static final String ABHA_SYSTEM = FhirConstants.ABHA_SYSTEM;

    /**
     * Converts a validated {@link PatientDTO} into a FHIR R4 {@link Patient} resource.
     *
     * @param dto the input patient data from the hospital system; must not be null
     * @return a fully populated HAPI FHIR {@link Patient} resource
     * @throws InvalidDateException if {@code dateOfBirth} cannot be parsed as yyyy-MM-dd
     */
    public Patient toFhirPatient(PatientDTO dto) {
        Patient patient = new Patient();

        // --- Logical ID ---
        patient.setId(dto.getPatientId());

        // --- Hospital identifier ---
        patient.addIdentifier(buildHospitalIdentifier(dto.getPatientId()));

        // --- ABHA number (optional) ---
        if (dto.getAbhaNumber() != null && !dto.getAbhaNumber().isBlank()) {
            patient.addIdentifier(buildAbhaIdentifier(dto.getAbhaNumber()));
        }

        // --- Full name ---
        patient.addName(buildHumanName(dto.getFullName()));

        // --- Gender ---
        patient.setGender(resolveGender(dto.getGender()));

        // --- Date of birth ---
        patient.setBirthDateElement(parseBirthDate(dto.getDateOfBirth()));

        // --- Phone number (optional) ---
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isBlank()) {
            patient.addTelecom(buildPhone(dto.getPhoneNumber()));
        }

        // --- Address (optional) ---
        if (dto.getAddress() != null && !dto.getAddress().isBlank()) {
            patient.addAddress(buildAddress(dto.getAddress()));
        }

        return patient;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Identifier buildHospitalIdentifier(String patientId) {
        return new Identifier()
                .setSystem(HOSPITAL_PATIENT_SYSTEM)
                .setValue(patientId)
                .setUse(Identifier.IdentifierUse.USUAL);
    }

    private Identifier buildAbhaIdentifier(String abhaNumber) {
        return new Identifier()
                .setSystem(ABHA_SYSTEM)
                .setValue(abhaNumber)
                .setUse(Identifier.IdentifierUse.OFFICIAL);
    }

    private HumanName buildHumanName(String fullName) {
        return new HumanName()
                .setUse(HumanName.NameUse.OFFICIAL)
                .setText(fullName);
    }

    private AdministrativeGender resolveGender(String gender) {
        return switch (gender.toLowerCase()) {
            case "male"    -> AdministrativeGender.MALE;
            case "female"  -> AdministrativeGender.FEMALE;
            case "other"   -> AdministrativeGender.OTHER;
            default        -> AdministrativeGender.UNKNOWN;
        };
    }

    /**
     * Parses an ISO-8601 date string (yyyy-MM-dd) into a FHIR {@link DateType}.
     *
     * @param dateOfBirth date string from the DTO
     * @return a FHIR {@link DateType}
     * @throws InvalidDateException if the string is not a valid calendar date
     */
    private DateType parseBirthDate(String dateOfBirth) {
        try {
            LocalDate.parse(dateOfBirth); // validates calendar correctness (e.g. no 2024-02-30)
            return new DateType(dateOfBirth);
        } catch (DateTimeParseException ex) {
            throw new InvalidDateException(
                    "Invalid dateOfBirth value: '" + dateOfBirth + "'. Expected yyyy-MM-dd.");
        }
    }

    private ContactPoint buildPhone(String phoneNumber) {
        return new ContactPoint()
                .setSystem(ContactPoint.ContactPointSystem.PHONE)
                .setValue(phoneNumber)
                .setUse(ContactPoint.ContactPointUse.HOME);
    }

    private Address buildAddress(String addressLine) {
        return new Address()
                .setUse(Address.AddressUse.HOME)
                .setText(addressLine);
    }
}
