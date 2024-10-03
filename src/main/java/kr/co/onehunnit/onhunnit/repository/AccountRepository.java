package kr.co.onehunnit.onhunnit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.onehunnit.onhunnit.domain.account.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

	//ToDo provider로 가져오는 메서드 필요
	boolean existsByEmail(String email);

	Optional<Account> findByNickname(String nickname);

}
