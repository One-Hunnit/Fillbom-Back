package kr.co.onehunnit.onhunnit.dto.account;

import lombok.Builder;
import lombok.Getter;

public class AccountRequestDto {

	@Getter
	@Builder
	public static class SignUp {
		private String email;
		private String provider;
		private String name;
		private int age;
		private String gender;
		private String phone;
		private String birthday;
	}

}
