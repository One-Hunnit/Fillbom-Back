package kr.co.onehunnit.onhunnit.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.interfaces.Claim;

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

@Transactional
@Service
@RequiredArgsConstructor
public class OAuthService {

	private final KakaoSocialService kakaoSocialService;
	private final AppleSocialService appleSocialService;
	private final AccountRepository accountRepository;
	private final JwtTokenProvider jwtTokenProvider;

	public TokenInfoDto kakaoOAuthLogin(String idToken) {
		HashMap<String, Object> kakaoUserInfo = kakaoSocialService.getKakaoUserInfo(idToken);
		String email = kakaoUserInfo.get("email").toString();
		Provider provider = Provider.KAKAO;

		if (isNewAccount(email, provider)) {
			saveAccount(email, provider);
		}
		return jwtTokenProvider.generateToken(getAuthentication(email, String.valueOf(provider)));
	}

	public TokenInfoDto appleOAuthLogin(String idToken) {
		Map<String, Claim> appleUserInfo = appleSocialService.decodeAppleIdToken(idToken).getClaims();
		String email = appleUserInfo.get("email").asString();
		Provider provider = Provider.APPLE;

		validateEmail(appleUserInfo);

		if (isNewAccount(email, provider)) {
			saveAccount(email, provider);
		}
		return jwtTokenProvider.generateToken(getAuthentication(email, String.valueOf(provider)));
	}

	private void validateEmail(Map<String, Claim> appleUserInfo) {
		Boolean emailVerified = appleUserInfo.get("email_verified").asBoolean();
		if (emailVerified == null || !emailVerified) {
			throw new ApiException(ErrorCode.NOT_VALIDATE_EMAIL);
		}
	}

	private boolean isNewAccount(String email, Provider provider) {
		return accountRepository.notExistsAccountByEmailAndProvider(email, provider);
	}

	private void saveAccount(String email, Provider provider) {
		Account newAccount = createAccount(email, provider);
		accountRepository.save(newAccount);
	}

	private Account createAccount(String email, Provider provider) {
		return Account.builder()
			.provider(provider)
			.email(email)
			.build();
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
