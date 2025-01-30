package kr.co.onehunnit.onhunnit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.co.onehunnit.onhunnit.domain.diary.Diary;
import kr.co.onehunnit.onhunnit.domain.patient.Patient;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

	List<Diary> findAllByPatientAndSharedTrueOrderByCreatedAtDesc(Patient patient);

	@Query("select d from Diary d where d.patient = :patient and d.shared = true and extract(MONTH from d.createdAt) = :month and extract(YEAR from d.createdAt) = :year order by d.createdAt desc")
	List<Diary> findAllByPatientAndDateAndSharedTrue(@Param("patient") Patient patient, @Param("month") int month, int year);

	@Query("select d from Diary d where d.patient = :patient and extract(MONTH from d.createdAt) = :month and extract(YEAR from d.createdAt) = :year order by d.createdAt desc")
	List<Diary> findAllByPatientAndDate(@Param("patient") Patient patient, @Param("month") int month, int year);

}
