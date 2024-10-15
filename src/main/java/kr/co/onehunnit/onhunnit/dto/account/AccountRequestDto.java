package kr.co.onehunnit.onhunnit.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.onehunnit.onhunnit.domain.account.Gender;
import kr.co.onehunnit.onhunnit.domain.account.Status;
import lombok.Builder;
import lombok.Getter;

public class AccountRequestDto {

	@Getter
	@Builder
	public static class SignUp {
		@Schema(description = "이메일")
		private String email;

		@Schema(description = "소셜 미디어(KAKAO,APPLE)")
		private String provider;

		@Schema(description = "이름")
		private String name;

		@Schema(description = "나이")
		private int age;

		@Schema(description = "성별(MAN,WOMAN)")
		private String gender;

		@Schema(description = "전화번호")
		private String phone;

		@Schema(description = "생년월일", example = "1999.09.13")
		private String birthday;
	}

	@Getter
	@Builder
	public static class Update {
		private String nickname;
		private String profileImage;
		private String phone;
		private String birthday;
		private Gender gender;
		private Status status;
	}

}
