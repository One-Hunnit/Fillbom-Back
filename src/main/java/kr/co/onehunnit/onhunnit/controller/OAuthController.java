package kr.co.onehunnit.onhunnit.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.onehunnit.onhunnit.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/oauth")
public class OAuthController {

	private final OAuthService oAuthService;

	// @GetMapping("/kakao")
	// public ResponseDto<TokenDto> kakaoCallback(@RequestParam(name = "code") String code) {
	// 	oAuthService.kakaoOAuthLogin(code);
	// 	return ResponseUtil.SUCCESS("카카오 로그인에 성공하였습니다.", new TokenDto());
	// }

	@GetMapping("/kakao")
	public void test(@RequestParam(name = "authorizationCode") String authorizationCode) {
		oAuthService.kakaoOAuthLogin(authorizationCode);
	}


}
