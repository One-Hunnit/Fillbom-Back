package kr.co.onehunnit.onhunnit.controller;

import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import kr.co.onehunnit.onhunnit.config.response.ResponseDto;
import kr.co.onehunnit.onhunnit.config.response.ResponseUtil;
import kr.co.onehunnit.onhunnit.dto.account.AccountResponseDto;
import kr.co.onehunnit.onhunnit.dto.account.AccountRequestDto;
import kr.co.onehunnit.onhunnit.dto.account.TokenAccountInfoDto;
import kr.co.onehunnit.onhunnit.service.AccountService;
import lombok.RequiredArgsConstructor;

@Tag(name = "계정")
@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
@Validated
public class AccountController {

	private final AccountService accountService;

	@Operation(summary = "회원가입")
	@PostMapping("/sign-up")
	public ResponseDto<String> signUp(HttpServletRequest request, @RequestBody AccountRequestDto.SignUp requestDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ApiException(ErrorCode.ACCOUNT_DATA_ERROR);
		}
		return ResponseUtil.SUCCESS("회원가입에 성공하였습니다.", accountService.signUp(request.getHeader("Authorization"), requestDto));
	}

	@Operation(summary = "계정 정보 조회", description = "jwt 토큰 필요")
	@GetMapping("/me")
	public ResponseDto<TokenAccountInfoDto> getUserInfo(HttpServletRequest request) {
		String accessToken = request.getHeader("Authorization");
		return ResponseUtil.SUCCESS("토큰정보 조회에 성공하였습니다.", accountService.getAccessTokenInfo(accessToken));
	}

	@Operation(summary = "계정 정보 수정", description = "jwt 토큰 필요")
	@PatchMapping("")
	public ResponseDto<AccountResponseDto.Info> updateUserInfo(HttpServletRequest request, @RequestBody AccountRequestDto.Update updateDto) {
		return ResponseUtil.SUCCESS("유저 정보 수정에 성공하였습니다.", accountService.updateUserInfo(request.getHeader("Authorization"), updateDto));
	}

	@Operation(summary = "회원탈퇴", description = "jwt 토큰 필요")
	@DeleteMapping("")
	public String deleteAccount(HttpServletRequest request) {
		accountService.deleteAccount(request.getHeader("Authorization"));
		return "회원탈퇴에 성공하였습니다.";
	}

}
