package kr.co.onehunnit.onhunnit.controller;

import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import kr.co.onehunnit.onhunnit.config.response.ResponseDto;
import kr.co.onehunnit.onhunnit.config.response.ResponseUtil;
import kr.co.onehunnit.onhunnit.dto.account.AccountRequestDto;
import kr.co.onehunnit.onhunnit.service.AccountService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
@Validated
//Todo /api/accounts/me jwt를 가지고 인증된 사용자의 정보 가져오는 api 필요
public class AccountController {

	private final AccountService accountService;

	@PostMapping("/sign-up")
	public ResponseDto<Long> signUp(@RequestBody AccountRequestDto.SignUp requestDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ApiException(ErrorCode.ACCOUNT_DATA_ERROR);
		}

		return ResponseUtil.SUCCESS("회원가입에 성공하였습니다.", accountService.signUp(requestDto));
	}

}
