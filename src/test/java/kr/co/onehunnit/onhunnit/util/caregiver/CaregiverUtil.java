package kr.co.onehunnit.onhunnit.util.caregiver;

import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.caregiver.Caregiver;

public class CaregiverUtil {

	public static Caregiver createCaregiver(String relationship, Account account) {
		return Caregiver.builder()
			.relationship(relationship)
			.account(account)
			.build();
	}

}
