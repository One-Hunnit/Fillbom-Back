package kr.co.onehunnit.onhunnit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.co.onehunnit.onhunnit.domain.patient.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

	Optional<Patient> findByAccount_Id(Long id);

	boolean existsByAccount_Id(Long id);

	@Query("select p from Patient p join p.account a where a.phone = :phone")
	List<Patient> findAllByPhoneNumber(@Param("phone") String phone);

}
