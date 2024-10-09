package kr.co.onehunnit.onhunnit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.onehunnit.onhunnit.domain.account.Caregiver;

@Repository
public interface CaregiverRepository extends JpaRepository<Caregiver, Long> {

	Optional<Caregiver> findByAccount_Id(Long id);

	boolean existsByAccount_Id(Long id);

}
