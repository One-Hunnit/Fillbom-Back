package kr.co.onehunnit.onhunnit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.onehunnit.onhunnit.domain.diary.Diary;
import kr.co.onehunnit.onhunnit.domain.patient.Patient;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

	List<Diary> findAllByPatientOrderByCreatedAtDesc(Patient patient);

	List<Diary> findAllByPatientAndSharedTrueOrderByCreatedAtDesc(Patient patient);

}
