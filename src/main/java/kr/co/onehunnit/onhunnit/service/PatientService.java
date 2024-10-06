package kr.co.onehunnit.onhunnit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.account.Caregiver;
import kr.co.onehunnit.onhunnit.domain.patient.Patient;
import kr.co.onehunnit.onhunnit.domain.patient_Caregiver.PatientCaregiver;
import kr.co.onehunnit.onhunnit.repository.CaregiverRepository;
import kr.co.onehunnit.onhunnit.repository.PatientCaregiverRepository;
import kr.co.onehunnit.onhunnit.repository.PatientRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class PatientService {

	private final PatientRepository patientRepository;
	private final AccountService accountService;
	private final CaregiverRepository caregiverRepository;
	private final PatientCaregiverRepository patientCaregiverRepositㅔㅁory;

	public Long registerCaregiver(String accessToken, Long caregiverId) {
		Account account = accountService.getAccountByToken(accessToken);
		Patient patient = patientRepository.findByAccount_Id(account.getId()).orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_PATIENT));
		Caregiver caregiver = caregiverRepository.findById(caregiverId).orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_CAREGIVER));

		PatientCaregiver patientCaregiver = new PatientCaregiver().builder()
			.patient(patient)
			.caregiver(caregiver)
			.is_accepted(true)
			.build();

		return patientCaregiverRepository.save(patientCaregiver).getId();
	}
}
