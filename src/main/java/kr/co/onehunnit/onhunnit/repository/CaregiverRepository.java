package kr.co.onehunnit.onhunnit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.onehunnit.onhunnit.domain.account.Caregiver;

@Repository
public interface CaregiverRepository extends JpaRepository<Caregiver, Long> {

}
