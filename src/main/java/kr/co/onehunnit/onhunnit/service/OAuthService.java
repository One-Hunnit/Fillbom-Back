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

import io.jsonwebtoken.JwtException;
import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import kr.co.onehunnit.onhunnit.config.jwt.JwtTokenProvider;
import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.account.Provider;
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
		}
		return jwtTokenProvider.generateToken(getAuthentication(email, String.valueOf(provider)));
	}

	private void saveAccount(HashMap<String, Object> kakaoUserInfo, String email) {
		String nickname = kakaoUserInfo.get("nickname").toString();
		String picture = kakaoUserInfo.get("picture").toString();
		Account newAccount = Account.builder()
			.provider(Provider.KAKAO)
			.email(email)
			.name(nickname)
			.profile_image(picture)
			.build();
		accountRepository.save(newAccount);
	}

	public TokenInfoDto reGenerateAccessToken(RefreshTokenDto refreshTokenDto) throws JwtException {
		String refreshToken = refreshTokenDto.getRefreshToken();
		if (!jwtTokenProvider.validateToken(refreshToken.substring(7).trim())) {
			throw new JwtException("유효하지 않은 토큰입니다.");
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
