package kr.co.onehunnit.onhunnit.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.onehunnit.onhunnit.config.response.ResponseDto;
import kr.co.onehunnit.onhunnit.config.response.ResponseUtil;
import kr.co.onehunnit.onhunnit.dto.token.RefreshTokenDto;
import kr.co.onehunnit.onhunnit.dto.token.TokenInfoDto;
import kr.co.onehunnit.onhunnit.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/oauth")
public class OAuthController {

	private final OAuthService oAuthService;

	@GetMapping("/kakao")
	public ResponseDto<TokenInfoDto> kakaoLogin(@RequestParam(name = "idToken") String idToken) {
		return ResponseUtil.SUCCESS("카카오 로그인에 성공하였습니다.", oAuthService.kakaoOAuthLogin(idToken));
	}

	@PostMapping("/refresh-token")
	public ResponseDto<TokenInfoDto> reGenerateAccessToken(@RequestBody RefreshTokenDto refreshTokenDto) {
		return ResponseUtil.SUCCESS("토큰을 재발급하였습니다.", oAuthService.reGenerateAccessToken(refreshTokenDto));
	}

}
