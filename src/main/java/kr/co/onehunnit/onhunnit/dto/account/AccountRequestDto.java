package kr.co.onehunnit.onhunnit.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.onehunnit.onhunnit.domain.account.Gender;
import lombok.Builder;
import lombok.Getter;

public class AccountRequestDto {

	@Getter
	@Builder
	public static class SignUp {

		@Schema(description = "이름")
		private String name;

		@Schema(description = "성별(MAN,WOMAN)")
		private String gender;

		@Schema(description = "전화번호")
		private String phone;

		@Schema(description = "생년월일", example = "1999.09.13")
		private String birthday;

		@Schema(description = "프로필이미지 url")
		private String profile_image;

		@Schema(description = "역할(PATIENT,CAREGIVER)")
		private String role;

	}

	@Getter
	@Builder
	public static class Update {
		private String name;
		private String profile_image;
		private String phone;
		private String birthday;
		private Gender gender;
	}

}
