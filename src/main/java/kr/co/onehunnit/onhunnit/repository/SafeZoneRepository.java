package kr.co.onehunnit.onhunnit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.onehunnit.onhunnit.domain.patient.Patient;
import kr.co.onehunnit.onhunnit.domain.safezone.SafeZone;

@Repository
public interface SafeZoneRepository extends JpaRepository<SafeZone, Long> {

	List<SafeZone> findAllByPatientOrderByCreatedAtDesc(Patient patient);

}
