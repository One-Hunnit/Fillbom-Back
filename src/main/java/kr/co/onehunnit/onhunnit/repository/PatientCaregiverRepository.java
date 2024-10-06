package kr.co.onehunnit.onhunnit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.onehunnit.onhunnit.domain.patient_Caregiver.PatientCaregiver;

@Repository
public interface PatientCaregiverRepository extends JpaRepository<PatientCaregiver, Long> {
}
