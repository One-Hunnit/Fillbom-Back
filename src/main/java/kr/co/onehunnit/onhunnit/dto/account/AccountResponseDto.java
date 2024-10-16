package kr.co.onehunnit.onhunnit.dto.account;

import kr.co.onehunnit.onhunnit.domain.account.Gender;
import kr.co.onehunnit.onhunnit.domain.account.Status;
import lombok.Builder;
import lombok.Getter;

public class AccountResponseDto {

	@Getter
	@Builder
	public static class Info {
		private Long id;
		private String email;
		private String name;
		private String phone;
		private String profile_image;
		private String birthday;
		private Gender gender;
		private Status status;
	}

}
