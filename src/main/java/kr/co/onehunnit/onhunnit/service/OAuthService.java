package kr.co.onehunnit.onhunnit.service;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import kr.co.onehunnit.onhunnit.dto.token.TokenDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthService {

	private final KakaoSocialService kakaoSocialService;

	public void kakaoOAuthLogin(String authorizationCode) {
		String kakaoToken = kakaoSocialService.getKakaoAccessToken(authorizationCode);
		HashMap<String, Object> userInfo = kakaoSocialService.getKakaoUserInfo(kakaoToken);
	}
}
