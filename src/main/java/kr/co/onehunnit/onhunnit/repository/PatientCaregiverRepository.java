package kr.co.onehunnit.onhunnit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.onehunnit.onhunnit.domain.caregiver.Caregiver;
import kr.co.onehunnit.onhunnit.domain.patient.Patient;
import kr.co.onehunnit.onhunnit.domain.patient_Caregiver.PatientCaregiver;

@Repository
public interface PatientCaregiverRepository extends JpaRepository<PatientCaregiver, Long> {

	Optional<PatientCaregiver> findByPatientAndCaregiver(Patient patient, Caregiver caregiver);

	List<PatientCaregiver> findAllByCaregiver(Caregiver caregiver);
}
