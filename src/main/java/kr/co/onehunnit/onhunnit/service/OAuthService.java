package kr.co.onehunnit.onhunnit.service;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import kr.co.onehunnit.onhunnit.config.jwt.JwtTokenProvider;
import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.account.SocialService;
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
	private final AuthenticationManager authenticationManager;

	public TokenInfoDto kakaoOAuthLogin(String authorizationCode) {
		String kakaoAccessToken = kakaoSocialService.getKakaoAccessToken(authorizationCode);
		HashMap<String, Object> kakaoUserInfo = kakaoSocialService.getKakaoUserInfo(kakaoAccessToken);
		String userNickname = kakaoUserInfo.get("nickname").toString();

		Optional<Account> account = accountRepository.findByNickname(userNickname);
		if (account.isEmpty()) {
			Account newAccount = Account.builder()
				.social_name(SocialService.KAKAO)
				.nickname(kakaoUserInfo.get("nickname").toString())
				.build();
			accountRepository.save(newAccount);
			return null;
		}

		return jwtTokenProvider.generateToken(getAuthentication(userNickname));
	}

	public TokenInfoDto reGenerateAccessToken(RefreshTokenDto refreshTokenDto) {
		String refreshToken = refreshTokenDto.getRefreshToken();
		if (!jwtTokenProvider.validateToken(refreshToken)) {
			throw new ApiException(ErrorCode.INVALID_TOKEN);
		}

		String userNickname = jwtTokenProvider.extractUsernameFromJwt(refreshToken);
		return jwtTokenProvider.generateToken(getAuthentication(userNickname));
	}

	private static Authentication getAuthentication(String userNickname) {
		Authentication authentication =	new UsernamePasswordAuthenticationToken(userNickname, "");
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return authentication;
	}
}
