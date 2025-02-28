package kr.co.onehunnit.onhunnit.service;

import static kr.co.onehunnit.onhunnit.domain.notification.Type.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import kr.co.onehunnit.onhunnit.config.redis.RedisUtils;
import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.caregiver.Caregiver;
import kr.co.onehunnit.onhunnit.domain.patient.Patient;
import kr.co.onehunnit.onhunnit.domain.patient_Caregiver.PatientCaregiver;
import kr.co.onehunnit.onhunnit.dto.caregiver.response.CaregiverResponseDto;
import kr.co.onehunnit.onhunnit.dto.notification.NotificationRequestDto;
import kr.co.onehunnit.onhunnit.dto.patient.PatientResponseDto;
import kr.co.onehunnit.onhunnit.repository.CaregiverRepository;
import kr.co.onehunnit.onhunnit.repository.PatientCaregiverRepository;
import kr.co.onehunnit.onhunnit.repository.PatientRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CaregiverService {

	private final RedisUtils redisUtils;
	private final NotificationService notificationService;
	private final CaregiverRepository caregiverRepository;
	private final PatientRepository patientRepository;
	private final PatientCaregiverRepository patientCaregiverRepository;

	@Transactional
	public CaregiverResponseDto registerPatient(Account account, Long patientId, String relationship) {
		Patient patient = patientRepository.findById(patientId)
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_PATIENT));
		Caregiver caregiver = caregiverRepository.findByAccount_Id(account.getId())
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_CAREGIVER));

		Long patientCaregiverId;
		if (!patientCaregiverRepository.findByPatientAndCaregiver(patient, caregiver).isPresent()) {
			// throw new ApiException(ErrorCode.ALREADY_EXISTS_PATIENT_CAREGIVER);
			PatientCaregiver patientCaregiver = PatientCaregiver.builder()
				.patient(patient)
				.caregiver(caregiver)
				.relationship(relationship)
				.isAccepted(false)
				.build();

			patientCaregiverId = patientCaregiverRepository.save(patientCaregiver).getId();
		} else {
			patientCaregiverId = patientCaregiverRepository.findByPatientAndCaregiver(patient, caregiver).get().getId();
		}


		String deviceToken = redisUtils.getDeviceTokenByAccountID(patientId);

		NotificationRequestDto.Info notificationInfo = NotificationRequestDto.Info.builder()
			.title(account.getName() + "님이 보호자 추가를 요청하였습니다.")
			.type(RELATIONSHIP_REQUEST)
			.senderId(account.getId())
			.receiverId(patient.getAccount().getId())
			.body("") //todo 추가 예정
			.build();

		notificationService.sendPushNotification(deviceToken, notificationInfo);
		notificationService.saveNotification(notificationInfo, account, patient.getAccount());

		return CaregiverResponseDto.of(patientCaregiverId);
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

		return PatientResponseDto.BriefDetail.of(patient.getAccount(), patientCaregiver);
	}

	public void deletePatient(Account account, Long patientId) {
		Patient patient = patientRepository.findById(patientId)
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_PATIENT));

		Caregiver caregiver = caregiverRepository.findByAccount_Id(account.getId())
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_CAREGIVER));

		if (isNotCaregiverOfPatient(caregiver, patient)) {
			throw new ApiException(ErrorCode.UNAUTHORIZED_ACCESS);
		}

		patientCaregiverRepository.deleteByPatient(patient);
	}

	private boolean isNotCaregiverOfPatient(Caregiver caregiver, Patient patient) {
		return caregiver.getPatientCaregiverList().stream()
			.noneMatch(pc -> pc.getPatient().equals(patient));
	}

	public CaregiverResponseDto.Id getCaregiverId(Account account) {
		Caregiver caregiver = caregiverRepository.findByAccount_Id(account.getId())
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_CAREGIVER));

		return CaregiverResponseDto.Id.of(caregiver.getId());
	}
}
