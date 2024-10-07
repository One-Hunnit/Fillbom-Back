package kr.co.onehunnit.onhunnit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.account.Provider;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

	Optional<Account> findByEmailAndProvider(String email, Provider provider);

	boolean existsAccountByEmailAndProvider(String email, Provider provider);

	@Query("select case when count(a) = 0 then true else false end from Account a where a.email = :email and a.provider = :provider")
	boolean notExistsAccountByEmailAndProvider(String email, Provider provider);

}
