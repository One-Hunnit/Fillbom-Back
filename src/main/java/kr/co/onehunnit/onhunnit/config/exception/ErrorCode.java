package kr.co.onehunnit.onhunnit.config.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	POSTS_EMPTY_TITLE(400, "제목을 입력하세요.", 417),
	INVALID_TOKEN(401, "유효하지 않은 토큰입니다.", 1001),
	UNKNOWN_ERROR(401, "토큰이 존재하지 않습니다.", 1002),
	WRONG_TYPE_TOKEN(401, "변조된 토큰입니다.", 1003),
	EXPIRED_TOKEN(401, "만료된 토큰입니다.", 1004),
	EXPIRED_REFRESH_TOKEN(401, "만료된 Refresh Token입니다. 로그인부터 다시 진행해주세요.", 1005),
	UNSUPPORTED_TOKEN(401, "변조된 토큰입니다.", 1006),
	ACCESS_DENIED(401, "권한이 없습니다.", 1007),
	NO_INFO(401, "토큰에 해당하는 정보가 없습니다.", 1008),
	FAILED_TO_RETRIEVE_KAKAO_ACCESS_TOKEN(401, "카카오로부터 AccessToken 발급에 실패했습니다.", 1009),
	RESPONSE_CODE_ERROR(401, "인가 코드 요청에 따른 응답 코드가 200이 아닙니다.", 1010),
	FAILED_TO_RETRIEVE_KAKAO_USER_INFO(401, "카카오로부터 유저 정보 발급에 실패했습니다.", 1011),
	;



	private final int status;
	private final String message;
	private final int code;

	ErrorCode(int status, String message) {
		this.status = status;
		this.message = message;
		this.code = status;
	}
}
