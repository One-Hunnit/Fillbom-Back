package kr.co.onehunnit.onhunnit.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

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

	public TokenInfoDto kakaoOAuthLogin(String authorizationCode) {
		String kakaoAccessToken = kakaoSocialService.getKakaoAccessToken(authorizationCode);
		//ToDo 로그인 명세에 맞게 수정 필요 Provider와 email을 사용하여 식별
		HashMap<String, Object> kakaoUserInfo = kakaoSocialService.getKakaoUserInfo(kakaoAccessToken);
		String userNickname = kakaoUserInfo.get("nickname").toString();

		Optional<Account> account = accountRepository.findByNickname(userNickname);
		if (account.isEmpty()) {
			Account newAccount = Account.builder()
				.provider(Provider.KAKAO)
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

	private Authentication getAuthentication(String userNickname) {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

		Authentication authentication = new UsernamePasswordAuthenticationToken(userNickname, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return authentication;
	}

}
