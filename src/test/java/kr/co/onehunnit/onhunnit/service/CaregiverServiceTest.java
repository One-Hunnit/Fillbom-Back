package kr.co.onehunnit.onhunnit.service;

import static kr.co.onehunnit.onhunnit.domain.account.Provider.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
class CaregiverServiceTest {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private CaregiverRepository caregiverRepository;

	@Autowired
	private PatientCaregiverRepository patientCaregiverRepository;

	@Autowired
	private CaregiverService caregiverService;

	@DisplayName("보호자는 환자를 등록할 수 있다. 단, 환자가 수락하기 전에는 isAccepted는 false이다.")
	@Test
	void registerPatient() {
		//given
		Account patientAccount = AccountUtil.createAccount("patient@daum.net", KAKAO);
		Account caregiverAccount = AccountUtil.createAccount("caregiver@daum.net", KAKAO);
		accountRepository.saveAll(List.of(patientAccount, caregiverAccount));

		Patient patient = PatientUtil.createPatient("알츠하이머", patientAccount);
		Long patientId = patientRepository.save(patient).getId();

		Caregiver caregiver = CaregiverUtil.createCaregiver("아들", caregiverAccount);
		caregiverRepository.save(caregiver).getId();

		//when
		Long patientCaregiverId = caregiverService.registerPatient(caregiverAccount, patientId, "모자");

		//then
		assertThat(patientCaregiverRepository.findById(patientCaregiverId).get())
			.extracting("relationship", "isAccepted")
			.contains("모자", false);
	}

	@DisplayName("보호자는 등록을 수락한 환자의 목록만 조회할 수 있다. 등록을 수락하지 않으면 조회할 수 없다.")
	@Test
	void getPatientsListOnlyAcceptedTrue() {
		//given
		Account patient1Account = AccountUtil.createAccount("patient1@daum.net", "김필봄", "profileImageUrl1");
		Account patient2Account = AccountUtil.createAccount("patient2@daum.net", "김필순", "profileImageUrl2");
		Account patient3Account = AccountUtil.createAccount("patient3@daum.net", "김필자", "profileImageUrl3");
		Account caregiverAccount = AccountUtil.createAccount("caregiver@daum.net", KAKAO);
		accountRepository.saveAll(List.of(patient1Account, patient2Account, patient3Account, caregiverAccount));

		Patient patient1 = PatientUtil.createPatient("알츠하이머", patient1Account);
		Patient patient2 = PatientUtil.createPatient("치매", patient2Account);
		Patient patient3 = PatientUtil.createPatient("치매", patient3Account);
		patientRepository.saveAll(List.of(patient1, patient2, patient3));

		Caregiver caregiver = CaregiverUtil.createCaregiver("아들", caregiverAccount);
		caregiverRepository.save(caregiver);

		PatientCaregiver patientCaregiver1 = PatientCaregiverUtil.createPatientCaregiver(patient1, caregiver, "모자", true);
		PatientCaregiver patientCaregiver2 = PatientCaregiverUtil.createPatientCaregiver(patient2, caregiver, "모자", true);
		PatientCaregiver patientCaregiver3 = PatientCaregiverUtil.createPatientCaregiver(patient3, caregiver, "모자", false);
		patientCaregiverRepository.saveAll(List.of(patientCaregiver1, patientCaregiver2, patientCaregiver3));

		//when
		List<PatientResponseDto.BriefDetail> patientsList = caregiverService.getPatientsList(caregiverAccount);

		//then
		assertThat(patientsList).hasSize(2)
			.extracting("name", "profileImageUrl", "relationship", "isAccepted")
			.containsExactlyInAnyOrder(
				tuple("김필봄", "profileImageUrl1", "모자", true),
				tuple("김필순", "profileImageUrl2", "모자", true)
			);
	}

}