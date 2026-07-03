package in.gov.abdm.fhir.hms.repository;

import in.gov.abdm.fhir.hms.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Read-only Spring Data JPA repository for the HMS {@code doctors} table.
 *
 * <p>Only read operations are used. No create / update / delete is performed
 * by the FHIR service — the HMS owns this data.</p>
 *
 * <p><strong>Dependency rule:</strong> Only {@code DummyHmsProvider} may import
 * this repository. No FHIR service or mapper may import it.</p>
 */
public interface HmsDoctorRepository extends JpaRepository<Doctor, Long> {
}
