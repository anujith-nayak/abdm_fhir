package in.gov.abdm.fhir.hms.repository;

import in.gov.abdm.fhir.hms.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Read-only Spring Data JPA repository for the HMS {@code patients} table.
 *
 * <p>Only read operations are used. No create / update / delete is performed
 * by the FHIR service — the HMS owns this data.</p>
 *
 * <p><strong>Dependency rule:</strong> Only {@code DummyHmsProvider} may import
 * this repository. No FHIR service or mapper may import it.</p>
 */
public interface HmsPatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Looks up a patient by their ABHA number.
     *
     * @param abhaNumber the ABHA number (unique per patient)
     * @return the matching patient, or empty if not found
     */
    Optional<Patient> findByAbhaNumber(String abhaNumber);
}
