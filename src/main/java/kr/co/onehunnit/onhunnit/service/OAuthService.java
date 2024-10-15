package kr.co.onehunnit.onhunnit.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import kr.co.onehunnit.onhunnit.config.jwt.JwtTokenProvider;
import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.account.Provider;
import kr.co.onehunnit.onhunnit.domain.account.Status;
import kr.co.onehunnit.onhunnit.dto.account.TokenAccountInfoDto;
import kr.co.onehunnit.onhunnit.dto.token.RefreshTokenDto;
import kr.co.onehunnit.onhunnit.dto.token.TokenInfoDto;
import kr.co.onehunnit.onhunnit.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthService {

	private final KakaoSocialService kakaoSocialService;
	private final AccountRepository accountRepository;
	private final JwtTokenProvider jwtTokenProvider;

	public TokenInfoDto kakaoOAuthLogin(String idToken) {
		HashMap<String, Object> kakaoUserInfo = kakaoSocialService.getKakaoUserInfo(idToken);
		String email = kakaoUserInfo.get("email").toString();
		Provider provider = Provider.KAKAO;

		if (accountRepository.notExistsAccountByEmailAndProvider(email, provider)) {
			saveAccount(kakaoUserInfo, email);
			return null;
		}
		return jwtTokenProvider.generateToken(getAuthentication(email, String.valueOf(provider)));
	}

	private void saveAccount(HashMap<String, Object> kakaoUserInfo, String email) {
		String nickname = kakaoUserInfo.get("nickname").toString();
		String picture = kakaoUserInfo.get("picture").toString();
		Account newAccount = Account.builder()
			.provider(Provider.KAKAO)
			.email(email)
			.nickname(nickname)
			.profile_image(picture)
			.status(Status.SIGNUP_PENDING)
			.build();
		accountRepository.save(newAccount);
	}

	public TokenInfoDto reGenerateAccessToken(RefreshTokenDto refreshTokenDto) {
		String refreshToken = refreshTokenDto.getRefreshToken();
		if (!jwtTokenProvider.validateToken(refreshToken.substring(7).trim())) {
			throw new ApiException(ErrorCode.INVALID_TOKEN);
		}

		TokenAccountInfoDto.TokenInfo tokenInfoDto = jwtTokenProvider.extractTokenInfoFromJwt(refreshToken);
		String email = tokenInfoDto.getEmail();
		String provider = tokenInfoDto.getProvider();
		return jwtTokenProvider.generateToken(getAuthentication(email, provider));
	}

	private Authentication getAuthentication(String email, String provider) {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

		Authentication authentication
			= new UsernamePasswordAuthenticationToken(email + "," + provider, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return authentication;
	}

}
