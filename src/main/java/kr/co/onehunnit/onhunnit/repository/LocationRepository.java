package kr.co.onehunnit.onhunnit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.onehunnit.onhunnit.domain.location.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

	void deleteAllByPatientId(Long patientId);

}
