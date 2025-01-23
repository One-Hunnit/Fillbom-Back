package kr.co.onehunnit.onhunnit.util.account;

import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.account.Gender;
import kr.co.onehunnit.onhunnit.domain.account.Provider;

public class AccountUtil {

	public static Account createAccount(String email, Provider provider) {
		return Account.builder()
			.email(email)
			.provider(provider)
			.build();
	}

	public static Account createAccount(String email, String name, String profileImageUrl) {
		return Account.builder()
			.provider(Provider.KAKAO)
			.email(email)
			.name(name)
			.profileImageUrl(profileImageUrl)
			.build();
	}

	public static Account createAccount(String email, Provider provider, String phone) {
		return Account.builder()
			.email(email)
			.provider(provider)
			.phone(phone)
			.build();
	}

	public static Account createAccount(String email, Provider provider, String profileImageUrl, String name, Gender gender, String birthday, String phoneNumber) {
		return Account.builder()
			.email(email)
			.provider(provider)
			.profileImageUrl(profileImageUrl)
			.name(name)
			.gender(gender)
			.birthday(birthday)
			.phone(phoneNumber)
			.build();
	}


}
