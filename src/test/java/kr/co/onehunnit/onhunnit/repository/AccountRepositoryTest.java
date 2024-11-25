package kr.co.onehunnit.onhunnit.repository;

import static kr.co.onehunnit.onhunnit.domain.account.Provider.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.account.Provider;

@ActiveProfiles("test")
@SpringBootTest
class AccountRepositoryTest {

	@Autowired
	private AccountRepository accountRepository;

	@AfterEach
	void tearDown() {
		accountRepository.deleteAllInBatch();
	}

	@DisplayName("이메일과 Provider로 유저를 조회한다.")
	@Test
	void findByEmailAndProvider() {
		// given
		String email = "test@daum.net";
		Provider provider = KAKAO;
		Account account = createAccount(email, provider);
		accountRepository.save(account);

		// when
		Optional<Account> findAccount = accountRepository.findByEmailAndProvider(email, provider);

		// then
		assertThat(findAccount).isPresent();
		assertThat(findAccount.get().getEmail()).isEqualTo(email);
		assertThat(findAccount.get().getProvider()).isEqualTo(provider);
	}

	@DisplayName("이메일과 Provider 정보가 없다면 True를 반환한다.")
	@Test
	void notExistsAccountByEmailAndProvider() {
		// given
		String email = "test@daum.net";
		Provider provider = KAKAO;

		//when
		boolean isSignedUp = accountRepository.notExistsAccountByEmailAndProvider(email, provider);

		//then
		assertThat(isSignedUp).isTrue();
	}

	private Account createAccount(String email, Provider provider) {
		return Account.builder()
			.email(email)
			.provider(provider)
			.build();
	}

}