package kr.co.onehunnit.onhunnit.util.account;

import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.account.Provider;

public class AccountUtil {

	public static Account createAccount(String email, Provider provider) {
		return Account.builder()
			.email(email)
			.provider(provider)
			.build();
	}

	public static Account createAccount(String email, String nickname, String picture) {
		return Account.builder()
			.provider(Provider.KAKAO)
			.email(email)
			.name(nickname)
			.profile_image(picture)
			.build();
	}


}
