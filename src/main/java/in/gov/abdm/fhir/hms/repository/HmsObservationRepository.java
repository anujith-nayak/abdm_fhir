package in.gov.abdm.fhir.hms.repository;

import in.gov.abdm.fhir.hms.entity.Observation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Read-only Spring Data JPA repository for the HMS {@code observations} table.
 *
 * <p>Only read operations are used. No create / update / delete is performed
 * by the FHIR service — the HMS owns this data.</p>
 *
 * <p><strong>Dependency rule:</strong> Only {@code DummyHmsProvider} may import
 * this repository. No FHIR service or mapper may import it.</p>
 */
public interface HmsObservationRepository extends JpaRepository<Observation, Long> {

    /**
     * Retrieves all observations for a given patient.
     *
     * @param patientId the patient's primary key
     * @return a list of observations (may be empty)
     */
    List<Observation> findByPatient_PatientId(Long patientId);
}
