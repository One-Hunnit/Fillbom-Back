package kr.co.onehunnit.onhunnit.repository;

import static kr.co.onehunnit.onhunnit.domain.account.Gender.*;
import static kr.co.onehunnit.onhunnit.domain.account.Provider.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.patient.Patient;
import kr.co.onehunnit.onhunnit.util.account.AccountUtil;
import kr.co.onehunnit.onhunnit.util.patient.PatientUtil;

@ActiveProfiles("test")
@DataJpaTest
class PatientRepositoryTest {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PatientRepository patientRepository;

	@DisplayName("AccountId로 환자를 조회한다.")
	@Test
	void findByAccountId() {
	    //given
		Account patientAccount = AccountUtil.createAccount("patient1@daum.net", KAKAO, "profileImage1", "김필봄", MAN, "1999.09.13", "01012345678");
		Long accountId = accountRepository.save(patientAccount).getId();

		Patient patient = PatientUtil.createPatient("알츠하이머", patientAccount);
		Long patientId = patientRepository.save(patient).getId();

		//when
		Patient findPatient = patientRepository.findByAccount_Id(accountId).get();

		//then
		assertThat(findPatient).isNotNull();
		assertThat(findPatient)
			.extracting("diagnosis", "id")
			.contains("알츠하이머", patientId);
	}

	@DisplayName("AccountId로 환자 데이터의 존재 여부를 확인할 수 있다.")
	@Test
	void checkExistenceByAccountId() {
	    //given
		Account patientAccount = AccountUtil.createAccount("patient1@daum.net", KAKAO, "profileImage1", "김필봄", MAN, "1999.09.13", "01012345678");
		Long accountId = accountRepository.save(patientAccount).getId();

		Patient patient = PatientUtil.createPatient("알츠하이머", patientAccount);
		patientRepository.save(patient);

	    //when
		boolean result1 = patientRepository.existsByAccount_Id(accountId);
		boolean result2 = patientRepository.existsByAccount_Id(2L);

		//then
		assertThat(result1).isTrue();
		assertThat(result2).isFalse();
	}

	@DisplayName("전화번호로 환자 리스트를 조회할 수 있다.")
	@Test
	void findAllByPhoneNumber() {
	    //given
		Account patientAccount1 = AccountUtil.createAccount("patient1@daum.net", KAKAO, "01012345678");
		Account patientAccount2 = AccountUtil.createAccount("patient2@daum.net", KAKAO, "01012348765");
		Account patientAccount3 = AccountUtil.createAccount("patient3@daum.net", KAKAO, "01098765432");
		accountRepository.saveAll(List.of(patientAccount1, patientAccount2, patientAccount3));

		Patient patient1 = PatientUtil.createPatient("알츠하이머", patientAccount1);
		Patient patient2 = PatientUtil.createPatient("알츠하이머", patientAccount2);
		Patient patient3 = PatientUtil.createPatient("알츠하이머", patientAccount3);
		patientRepository.saveAll(List.of(patient1, patient2, patient3));

	    //when
		List<Patient> list1 = patientRepository.findAllByPhoneNumber("1234");
		List<Patient> list2 = patientRepository.findAllByPhoneNumber("1");
		List<Patient> list3 = patientRepository.findAllByPhoneNumber("111");

		//then
		assertThat(list1).hasSize(2)
			.extracting("id")
			.containsExactlyInAnyOrder(patient1.getId(), patient2.getId());

		assertThat(list2).hasSize(3)
			.extracting("id")
			.containsExactlyInAnyOrder(patient1.getId(), patient2.getId(), patient3.getId());

		assertThat(list3).hasSize(0);
	}

}