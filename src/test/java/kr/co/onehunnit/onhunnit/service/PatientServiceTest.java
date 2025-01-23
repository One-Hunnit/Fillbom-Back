package kr.co.onehunnit.onhunnit.service;

import static kr.co.onehunnit.onhunnit.domain.account.Gender.*;
import static kr.co.onehunnit.onhunnit.domain.account.Provider.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.caregiver.Caregiver;
import kr.co.onehunnit.onhunnit.domain.patient.Patient;
import kr.co.onehunnit.onhunnit.domain.patient_Caregiver.PatientCaregiver;
import kr.co.onehunnit.onhunnit.dto.patient.PatientResponseDto;
import kr.co.onehunnit.onhunnit.repository.AccountRepository;
import kr.co.onehunnit.onhunnit.repository.CaregiverRepository;
import kr.co.onehunnit.onhunnit.repository.PatientCaregiverRepository;
import kr.co.onehunnit.onhunnit.repository.PatientRepository;
import kr.co.onehunnit.onhunnit.util.account.AccountUtil;
import kr.co.onehunnit.onhunnit.util.caregiver.CaregiverUtil;
import kr.co.onehunnit.onhunnit.util.patient.PatientUtil;
import kr.co.onehunnit.onhunnit.util.patientcaregiver.PatientCaregiverUtil;

@ActiveProfiles("test")
@SpringBootTest
class PatientServiceTest {

	@Autowired
	private PatientService patientService;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private CaregiverRepository caregiverRepository;

	@Autowired
	private PatientCaregiverRepository patientCaregiverRepository;

	@AfterEach
	void tearDown() {
		patientCaregiverRepository.deleteAllInBatch();
		patientRepository.deleteAllInBatch();
		caregiverRepository.deleteAllInBatch();
		accountRepository.deleteAllInBatch();
	}

	@BeforeEach
	void setUp() {
		Account patientAccount = AccountUtil.createAccount("patient@daum.net", KAKAO);
		Account caregiverAccount = AccountUtil.createAccount("caregiver@daum.net", KAKAO);
		accountRepository.saveAll(List.of(patientAccount, caregiverAccount));

	}

	@DisplayName("환자는 보호자의 요청을 수락해 등록을 할 수 있다.")
	@Test
	void registerCaregiver() {
		//given
		Account patientAccount = AccountUtil.createAccount("patient@daum.net", KAKAO);
		Account caregiverAccount = AccountUtil.createAccount("caregiver@daum.net", KAKAO);
		accountRepository.saveAll(List.of(patientAccount, caregiverAccount));

		Patient patient = PatientUtil.createPatient("알츠하이머", patientAccount);
		patientRepository.save(patient);

		Caregiver caregiver = CaregiverUtil.createCaregiver("아들", caregiverAccount);
		Long caregiverId = caregiverRepository.save(caregiver).getId();

		PatientCaregiver patientCaregiver = PatientCaregiverUtil.createPatientCaregiver(patient, caregiver, "아들", false);
		patientCaregiverRepository.save(patientCaregiver);

		//when
		Long patientCaregiverId = patientService.registerCaregiver(patientAccount, caregiverId);

		//then
		assertThat(patientCaregiverRepository.findById(patientCaregiverId).get().isAccepted()).isTrue();
		assertThat(patientCaregiverId).isEqualTo(patientCaregiver.getId());
	}

	@DisplayName("전화번호로 환자를 조회한다.")
	@Test
	void findPatientsByPhone() {
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
		List<PatientResponseDto.Phone> list1 = patientService.findPatientsByPhone("010");
		List<PatientResponseDto.Phone> list2 = patientService.findPatientsByPhone("1234");
		List<PatientResponseDto.Phone> list3 = patientService.findPatientsByPhone("3982");

		//then
		assertThat(list1).hasSize(3)
			.extracting("phoneNumber")
			.containsExactlyInAnyOrder(
				"01012345678",
				"01012348765",
				"01098765432"
			);

		assertThat(list2).hasSize(2)
			.extracting("phoneNumber")
			.containsExactlyInAnyOrder(
				"01012345678",
				"01012348765");

		assertThat(list3).hasSize(0);
	}

	@DisplayName("환자의 상세 정보를 조회할 수 있다.")
	@Test
	void seePatientDetails() {
	    //given
		Account patientAccount1 = AccountUtil.createAccount("patient1@daum.net", KAKAO, "profileImage1", "김필봄", MAN, "1999.09.13", "01012345678");
		Account caregiverAccount1 = AccountUtil.createAccount("caregiver1@daum.net", KAKAO);
		accountRepository.saveAll(List.of(patientAccount1, caregiverAccount1));

		Patient patient1 = PatientUtil.createPatient("알츠하이머", patientAccount1);
		Long patient1Id = patientRepository.save(patient1).getId();

		Caregiver caregiver1 = CaregiverUtil.createCaregiver("아들", caregiverAccount1);
		caregiverRepository.save(caregiver1);

		PatientCaregiver patientCaregiver = PatientCaregiverUtil.createPatientCaregiver(patient1, caregiver1, "모자", false);
		patientCaregiverRepository.save(patientCaregiver);

	    //when
		PatientResponseDto.Detail patientDetail = patientService.getPatientDetail(caregiverAccount1, patient1Id);

	    //then
		assertThat(patientDetail)
			.extracting("profileImageUrl", "name", "gender", "birthday", "phoneNumber")
			.contains("profileImage1", "김필봄", MAN, "1999.09.13", "01012345678");
	}

	@DisplayName("권한이 없는 환자의 정보는 조회할 수 없다.")
	@Test
	void cantSeePatientDetailsNotAuthorized() {
		//given
		Account patientAccount1 = AccountUtil.createAccount("patient1@daum.net", KAKAO, "profileImage1", "김필봄", MAN, "1999.09.13", "01012345678");
		Account patientAccount2 = AccountUtil.createAccount("patient2@daum.net", KAKAO, "profileImage2", "김필자", WOMAN, "1988.08.08", "01098765432");
		Account caregiverAccount1 = AccountUtil.createAccount("caregiver1@daum.net", KAKAO);
		accountRepository.saveAll(List.of(patientAccount1, patientAccount2, caregiverAccount1));

		Patient patient1 = PatientUtil.createPatient("알츠하이머", patientAccount1);
		Patient patient2 = PatientUtil.createPatient("알츠하이머", patientAccount2);
		patientRepository.saveAll(List.of(patient1, patient2));

		Caregiver caregiver1 = CaregiverUtil.createCaregiver("아들", caregiverAccount1);
		caregiverRepository.save(caregiver1);

		PatientCaregiver patientCaregiver = PatientCaregiverUtil.createPatientCaregiver(patient1, caregiver1, "모자", false);
		patientCaregiverRepository.save(patientCaregiver);

		//when //then
		assertThatThrownBy(() -> patientService.getPatientDetail(caregiverAccount1, patient2.getId()))
			.isInstanceOf(AccessDeniedException.class)
			.hasMessage("권한이 없는 환자의 정보를 조회할 수 없습니다.");
	}

}