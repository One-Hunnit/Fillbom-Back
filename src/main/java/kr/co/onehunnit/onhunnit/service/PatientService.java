package kr.co.onehunnit.onhunnit.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import kr.co.onehunnit.onhunnit.config.redis.RedisUtils;
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
@Service
@Transactional(isolation = Isolation.DEFAULT)
public class PatientService {

	private final PatientRepository patientRepository;
	private final CaregiverRepository caregiverRepository;
	private final PatientCaregiverRepository patientCaregiverRepository;
	private final RedisUtils redisUtils;

	public Long registerCaregiver(Account account, Long caregiverId) {
		Patient patient = patientRepository.findByAccount_Id(account.getId())
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_PATIENT));
		Caregiver caregiver = caregiverRepository.findById(caregiverId)
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_CAREGIVER));
		PatientCaregiver patientCaregiver = patientCaregiverRepository.findByPatientAndCaregiver(patient, caregiver)
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_PATIENT_CAREGIVER));

		patientCaregiver.register();
		return patientCaregiver.getId();
	}

	public List<PatientResponseDto.Phone> findPatientsByPhone(String phoneNumber) {
		List<Patient> patientList = patientRepository.findAllByPhoneNumber(phoneNumber);

		return patientList.stream()
			.map(Patient::getAccount)
			.filter(Objects::nonNull)
			.map(PatientResponseDto.Phone::of)
			.collect(Collectors.toList());
	}

	public PatientResponseDto.Detail getPatientDetail(Account account, Long patientId) {
		Caregiver caregiver = caregiverRepository.findByAccount_Id(account.getId())
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_CAREGIVER));
		Patient patient = patientRepository.findById(patientId)
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_PATIENT));

		if (isNotCaregiverOfPatient(caregiver, patient)) {
			throw new AccessDeniedException("권한이 없는 환자의 정보를 조회할 수 없습니다.");
		}

		PatientResponseDto.Location location = redisUtils.getLocationByPatientId(patientId);
		return PatientResponseDto.Detail.of(account, location);
	}

	private boolean isNotCaregiverOfPatient(Caregiver caregiver, Patient patient) {
		return caregiver.getPatientCaregiverList().stream()
			.noneMatch(pc -> pc.getPatient().equals(patient));
	}

}
