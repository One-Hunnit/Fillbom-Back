package kr.co.onehunnit.onhunnit.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.caregiver.Caregiver;
import kr.co.onehunnit.onhunnit.domain.patient.Patient;
import kr.co.onehunnit.onhunnit.domain.patient_Caregiver.PatientCaregiver;
import kr.co.onehunnit.onhunnit.dto.patient.PatientResponseDto;
import kr.co.onehunnit.onhunnit.repository.CaregiverRepository;
import kr.co.onehunnit.onhunnit.repository.PatientCaregiverRepository;
import kr.co.onehunnit.onhunnit.repository.PatientRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class CaregiverService {

	private final AccountService accountService;
	private final CaregiverRepository caregiverRepository;
	private final PatientRepository patientRepository;
	private final PatientCaregiverRepository patientCaregiverRepository;

	public Long registerPatient(Account account, Long patientId, String relationship) {
		Patient patient = patientRepository.findById(patientId)
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_PATIENT));
		Caregiver caregiver = caregiverRepository.findByAccount_Id(account.getId())
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_CAREGIVER));

		PatientCaregiver patientCaregiver = PatientCaregiver.builder()
			.patient(patient)
			.caregiver(caregiver)
			.relationship(relationship)
			.is_accepted(false)
			.build();

		return patientCaregiverRepository.save(patientCaregiver).getId();
	}

	public List<PatientResponseDto.BriefDetail> getPatientsList(Account account) {
		Caregiver caregiver = caregiverRepository.findByAccount_Id(account.getId())
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_CAREGIVER));

		return patientCaregiverRepository.findAllByCaregiver(caregiver).stream()
			.map(this::convertToBrief)
			.collect(Collectors.toList());
	}

	private PatientResponseDto.BriefDetail convertToBrief(PatientCaregiver patientCaregiver) {
		Patient patient = patientRepository.findById(patientCaregiver.getPatient().getId())
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_PATIENT));

		return PatientResponseDto.BriefDetail.builder()
			.profileImageUrl(patient.getAccount().getProfileImageUrl())
			.name(patient.getAccount().getName())
			.relationship(patientCaregiver.getRelationship())
			.isAccepted(patientCaregiver.is_accepted())
			.build();
	}
}
