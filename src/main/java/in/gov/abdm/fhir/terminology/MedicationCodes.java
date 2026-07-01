package in.gov.abdm.fhir.terminology;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

/**
 * Centralised medication coding support for the ABDM FHIR Service.
 *
 * <h2>Design philosophy</h2>
 * <p>ABDM M2/M3 does not yet mandate a single medication coding system.
 * This class is designed so that:
 * <ul>
 *   <li>A formal code (RxNorm, SNOMED, or ABDM) can be supplied alongside the
 *       free-text medicine name.</li>
 *   <li>When no formal code is available the resource degrades gracefully to a
 *       text-only {@link CodeableConcept}, which is valid FHIR R4.</li>
 *   <li>Swapping in a real coding system (Phase 4+) requires changing only this
 *       class and the mapper call-site — no structural changes needed.</li>
 * </ul>
 * </p>
 *
 * <h2>Supported coding systems (priority order)</h2>
 * <ol>
 *   <li>ABDM medication system ({@link FhirConstants#ABDM_MEDICATION_SYSTEM}) —
 *       use when NHA publishes official codes.</li>
 *   <li>RxNorm ({@link FhirConstants#RXNORM}) — international fallback.</li>
 *   <li>SNOMED CT ({@link FhirConstants#SNOMED_CT}) — for clinical drug concepts.</li>
 *   <li>Text-only — always acceptable per FHIR R4 §4.0.0.</li>
 * </ol>
 *
 * <p>TODO (Phase 4): Wire up a medication lookup service that returns an ABDM/RxNorm
 * code for a given medicine name, then call {@link #buildCodedConcept} instead of
 * {@link #buildTextOnlyConcept}.</p>
 */
public final class MedicationCodes {

    private MedicationCodes() {}

    /**
     * Builds a text-only {@link CodeableConcept} for a medication name.
     *
     * <p>This is the current production path. It is fully valid FHIR R4 and
     * interoperable — receivers are required to support text-only concepts.
     * Replace with {@link #buildCodedConcept} once formal codes are available.</p>
     *
     * @param medicineName the free-text medication name from the DTO
     * @return a text-only {@link CodeableConcept}
     */
    public static CodeableConcept buildTextOnlyConcept(String medicineName) {
        return new CodeableConcept().setText(medicineName);
    }

    /**
     * Builds a formally coded {@link CodeableConcept} for a medication.
     *
     * <p>Use this method when a real code (RxNorm, SNOMED, or ABDM) is available
     * for the medicine name. The text field is always populated as a human-readable
     * fallback per FHIR best practice.</p>
     *
     * @param medicineName the free-text medicine name (used as {@code text})
     * @param system       the coding system URI (use a constant from {@link FhirConstants})
     * @param code         the code within the system
     * @param display      the official display name for the code
     * @return a {@link CodeableConcept} with a formal coding plus text fallback
     */
    public static CodeableConcept buildCodedConcept(String medicineName,
                                                     String system,
                                                     String code,
                                                     String display) {
        return new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem(system)
                        .setCode(code)
                        .setDisplay(display))
                .setText(medicineName);
    }

    /**
     * Builds a {@link CodeableConcept} using the ABDM medication coding system.
     *
     * <p>This is the preferred path once ABDM publishes its medication code list.
     * Currently the same as calling
     * {@link #buildCodedConcept(String, String, String, String)} with
     * {@link FhirConstants#ABDM_MEDICATION_SYSTEM}.</p>
     *
     * @param medicineName the free-text medicine name
     * @param abdmCode     the ABDM medication code
     * @param display      the official ABDM display name
     * @return a coded {@link CodeableConcept}
     */
    public static CodeableConcept buildAbdmCodedConcept(String medicineName,
                                                         String abdmCode,
                                                         String display) {
        return buildCodedConcept(medicineName, FhirConstants.ABDM_MEDICATION_SYSTEM,
                abdmCode, display);
    }

    /**
     * Builds a {@link CodeableConcept} using the RxNorm coding system.
     *
     * @param medicineName the free-text medicine name
     * @param rxNormCode   the RxNorm concept unique identifier (RXCUI)
     * @param display      the RxNorm display name
     * @return a coded {@link CodeableConcept}
     */
    public static CodeableConcept buildRxNormConcept(String medicineName,
                                                      String rxNormCode,
                                                      String display) {
        return buildCodedConcept(medicineName, FhirConstants.RXNORM, rxNormCode, display);
    }
}
