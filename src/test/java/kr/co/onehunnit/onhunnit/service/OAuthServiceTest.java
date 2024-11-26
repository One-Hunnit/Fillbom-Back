package kr.co.onehunnit.onhunnit.service;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import kr.co.onehunnit.onhunnit.config.jwt.JwtTokenProvider;
import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.account.Provider;
import kr.co.onehunnit.onhunnit.dto.account.TokenAccountInfoDto;
import kr.co.onehunnit.onhunnit.dto.token.TokenInfoDto;
import kr.co.onehunnit.onhunnit.repository.AccountRepository;
import kr.co.onehunnit.onhunnit.util.account.AccountUtil;

@ActiveProfiles("test")
@SpringBootTest
class OAuthServiceTest {

	private String idToken = "";

	@Autowired
	private OAuthService oAuthService;

	@Autowired
	private KakaoSocialService kakaoSocialService;

	@Autowired
	private AccountRepository accountRepository;

	@AfterEach
	void tearDown() {
		accountRepository.deleteAllInBatch();
	}

	@DisplayName("처음 소셜로그인을 진행하는 사용자는 DB에 카카오 프로필 정보를 저장하며 JWT 토큰을 발급한다.")
	@Test
	void FirstTimekakaoOAuthLogin() {
		// given
		HashMap<String, Object> kakaoUserInfo = kakaoSocialService.getKakaoUserInfo(idToken);
		String email = kakaoUserInfo.get("email").toString();
		String nickname = kakaoUserInfo.get("nickname").toString();
		String picture = kakaoUserInfo.get("picture").toString();

		// when
		TokenInfoDto tokenInfoDto = oAuthService.kakaoOAuthLogin(idToken);

		//then
		assertThat(accountRepository.count()).isEqualTo(1);
		assertThat(accountRepository.findById(1L).get().getName()).isEqualTo(nickname);
		assertThat(accountRepository.findById(1L).get().getEmail()).isEqualTo(email);
		assertThat(accountRepository.findById(1L).get().getProfile_image()).isEqualTo(picture);
		assertThat(tokenInfoDto.getGrantType()).isEqualTo("Bearer");
		assertThat(tokenInfoDto.getAccessToken()).isNotNull();
		assertThat(tokenInfoDto.getRefreshToken()).isNotNull();
	}

	@DisplayName("기존 회원의 경우 소셜 로그인 진행 시 JWT 토큰만 발급한다.")
	@Test
	void kakaoOAuthLogin() {
		//given
		HashMap<String, Object> kakaoUserInfo = kakaoSocialService.getKakaoUserInfo(idToken);
		String email = kakaoUserInfo.get("email").toString();
		String nickname = kakaoUserInfo.get("nickname").toString();
		String picture = kakaoUserInfo.get("picture").toString();
		Account account = AccountUtil.createAccount(email, nickname, picture);
		accountRepository.save(account);

		//when
		TokenInfoDto tokenInfoDto = oAuthService.kakaoOAuthLogin(idToken);

		//then
		assertThat(accountRepository.count()).isEqualTo(1);
		assertThat(tokenInfoDto.getGrantType()).isEqualTo("Bearer");
		assertThat(tokenInfoDto.getAccessToken()).isNotNull();
		assertThat(tokenInfoDto.getRefreshToken()).isNotNull();
	}

}