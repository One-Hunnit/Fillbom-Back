package kr.co.onehunnit.onhunnit.dto.account;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class AccountRequestDto {

	@Getter
	@Builder
	public static class SignUp {
		private String email;
		private String name;
		private String phoneNumber;
	}

}
