package kr.co.onehunnit.onhunnit.util.patient;

import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.patient.Patient;

public class PatientUtil {

	public static Patient createPatient(String diagnosis, Account account) {
		return Patient.builder()
			.diagnosis(diagnosis)
			.account(account)
			.build();
	}

}
