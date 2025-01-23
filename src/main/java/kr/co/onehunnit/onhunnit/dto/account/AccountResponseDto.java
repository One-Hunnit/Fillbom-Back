package kr.co.onehunnit.onhunnit.dto.account;

import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.account.Gender;
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
		private String profileImageUrl;
		private String birthday;
		private Gender gender;

		public static AccountResponseDto.Info of(Account account) {
			return Info.builder()
				.id(account.getId())
				.email(account.getEmail())
				.phone(account.getPhone())
				.profileImageUrl(account.getProfileImageUrl())
				.name(account.getName())
				.birthday(account.getBirthday())
				.gender(account.getGender())
				.build();
		}
	}

}
