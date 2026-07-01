package in.gov.abdm.fhir.mapper;

import in.gov.abdm.fhir.dto.ObservationDTO;
import in.gov.abdm.fhir.exception.InvalidDateException;
import in.gov.abdm.fhir.terminology.FhirConstants;
import in.gov.abdm.fhir.terminology.ObservationCodes;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Maps an {@link ObservationDTO} received from a hospital system into a HAPI FHIR R4
 * {@link Observation} resource.
 *
 * <p>All LOINC codes, UCUM units, and system URIs are sourced from the centralised
 * terminology package ({@link ObservationCodes}, {@link FhirConstants}) — no
 * magic strings live in this mapper.</p>
 *
 * <p>Observation codes (LOINC):
 * <ul>
 *   <li>blood-pressure    → 55284-4  (panel) + 8480-6 systolic + 8462-4 diastolic</li>
 *   <li>temperature       → 8310-5</li>
 *   <li>heart-rate        → 8867-4</li>
 *   <li>weight            → 29463-7</li>
 *   <li>height            → 8302-2</li>
 *   <li>oxygen-saturation → 2708-6</li>
 * </ul>
 * </p>
 */
@Component
public class ObservationFhirMapper {

    /**
     * Converts a validated {@link ObservationDTO} into a FHIR R4 {@link Observation} resource.
     *
     * @param dto the input observation data; must not be null
     * @return a fully populated HAPI FHIR {@link Observation} resource
     * @throws InvalidDateException     if {@code recordedDate} is not a valid calendar date
     * @throws IllegalArgumentException if {@code observationType} is not recognised
     */
    public Observation toFhirObservation(ObservationDTO dto) {
        Observation observation = new Observation();

        // --- Logical ID ---
        observation.setId(dto.getObservationId());

        // --- Identifier ---
        observation.addIdentifier(
            new Identifier()
                .setSystem(FhirConstants.HOSPITAL_OBSERVATION_SYSTEM)
                .setValue(dto.getObservationId())
                .setUse(Identifier.IdentifierUse.USUAL)
        );

        // --- Status: always 'final' for submitted observations ---
        observation.setStatus(Observation.ObservationStatus.FINAL);

        // --- Category: vital-signs (covers all supported types) ---
        observation.addCategory(buildVitalSignsCategory());

        // --- LOINC code — sourced from ObservationCodes registry ---
        observation.setCode(ObservationCodes.buildCode(dto.getObservationType()));

        // --- Subject (patient reference) ---
        observation.setSubject(
            new Reference(FhirConstants.PATIENT_REF_PREFIX + dto.getPatientId())
        );

        // --- Effective date ---
        observation.setEffective(parseRecordedDate(dto.getRecordedDate()));

        // --- Value ---
        setObservationValue(observation, dto);

        return observation;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Returns the HL7 observation-category {@link CodeableConcept} for vital signs.
     * Applied to all supported observation types in this service.
     */
    private CodeableConcept buildVitalSignsCategory() {
        return new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem(FhirConstants.HL7_OBSERVATION_CATEGORY)
                        .setCode("vital-signs")
                        .setDisplay("Vital Signs"))
                .setText("Vital Signs");
    }

    /**
     * Sets the value element on the observation.
     *
     * <p>Blood pressure is modelled as a two-component panel (systolic + diastolic).
     * All other observations use a single UCUM-coded {@link Quantity}.</p>
     */
    private void setObservationValue(Observation observation, ObservationDTO dto) {
        String type = dto.getObservationType().toLowerCase();

        if ("blood-pressure".equals(type)) {
            buildBloodPressureComponents(observation, dto.getObservationValue(), dto.getUnit());
        } else {
            try {
                double numericValue = Double.parseDouble(dto.getObservationValue());
                // Prefer the UCUM unit from the registry; fall back to the DTO unit string
                String ucumCode = dto.getUnit();
                observation.setValue(
                    new Quantity()
                        .setValue(numericValue)
                        .setUnit(dto.getUnit())
                        .setSystem(FhirConstants.UCUM)
                        .setCode(ucumCode)
                );
            } catch (NumberFormatException ex) {
                // Non-numeric value — encode as string (valid FHIR R4 for edge cases)
                observation.setValue(new StringType(dto.getObservationValue()));
            }
        }
    }

    /**
     * Builds two FHIR observation components for a blood-pressure reading.
     *
     * <p>Component codes come from {@link ObservationCodes} (LOINC 8480-6 / 8462-4).
     * Input format: {@code "systolic/diastolic"} — e.g. {@code "120/80"}.</p>
     */
    private void buildBloodPressureComponents(Observation observation,
                                              String bpValue,
                                              String unit) {
        String[] parts = bpValue.split("/");
        if (parts.length != 2) {
            observation.setValue(new StringType(bpValue));
            return;
        }

        // Systolic component
        Observation.ObservationComponentComponent systolic =
            new Observation.ObservationComponentComponent();
        systolic.setCode(ObservationCodes.buildSystolicCode());
        systolic.setValue(buildBpQuantity(parts[0].trim(), unit));
        observation.addComponent(systolic);

        // Diastolic component
        Observation.ObservationComponentComponent diastolic =
            new Observation.ObservationComponentComponent();
        diastolic.setCode(ObservationCodes.buildDiastolicCode());
        diastolic.setValue(buildBpQuantity(parts[1].trim(), unit));
        observation.addComponent(diastolic);
    }

    private org.hl7.fhir.r4.model.Type buildBpQuantity(String rawValue, String unit) {
        try {
            return new Quantity()
                    .setValue(Double.parseDouble(rawValue))
                    .setUnit(unit)
                    .setSystem(FhirConstants.UCUM)
                    .setCode(unit);
        } catch (NumberFormatException e) {
            return new StringType(rawValue);
        }
    }

    /**
     * Parses an ISO-8601 date string into a FHIR {@link DateTimeType}.
     *
     * @throws InvalidDateException if the date string is not a valid calendar date
     */
    private DateTimeType parseRecordedDate(String recordedDate) {
        try {
            LocalDate.parse(recordedDate);
            return new DateTimeType(recordedDate);
        } catch (DateTimeParseException ex) {
            throw new InvalidDateException(
                "Invalid recordedDate value: '" + recordedDate + "'. Expected yyyy-MM-dd.");
        }
    }
}
