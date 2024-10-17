package kr.co.onehunnit.onhunnit.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.account.Caregiver;
import kr.co.onehunnit.onhunnit.domain.diary.Diary;
import kr.co.onehunnit.onhunnit.domain.patient.Patient;
import kr.co.onehunnit.onhunnit.dto.diary.DiaryResponseDto;
import kr.co.onehunnit.onhunnit.repository.CaregiverRepository;
import kr.co.onehunnit.onhunnit.repository.DiaryRepository;
import kr.co.onehunnit.onhunnit.repository.PatientRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DiaryServiceForCaregiver {

	private final DiaryRepository diaryRepository;
	private final PatientRepository patientRepository;
	private final CaregiverRepository caregiverRepository;

	private final AccountService accountService;

	public List<DiaryResponseDto.Brief> findPatientDiariesForCaregiver(String accessToken, Long patientId) {
		Caregiver caregiver = getCaregiverByAccessToken(accessToken);
		Patient patient = patientRepository.findById(patientId).orElseThrow(
			() -> new ApiException(ErrorCode.NOT_EXIST_PATIENT));

		if (isNotCaregiverOfPatient(caregiver, patient)) {
			throw new AccessDeniedException("권한이 없는 환자의 정보를 조회할 수 없습니다.");
		}

		return diaryRepository.findAllByPatientAndSharedTrueOrderByCreatedAtDesc(patient).stream()
			.map(this::convertToBriefDto)
			.collect(Collectors.toList());
	}

	public DiaryResponseDto.Detail findPatientDiaryForCaregiver(String accessToken, Long patientId,
		Long diaryId) {
		Caregiver caregiver = getCaregiverByAccessToken(accessToken);
		Patient patient = patientRepository.findById(patientId).orElseThrow(
			() -> new ApiException(ErrorCode.NOT_EXIST_PATIENT));

		if (isNotCaregiverOfPatient(caregiver, patient)) {
			throw new AccessDeniedException("권한이 없는 환자의 정보를 조회할 수 없습니다.");
		}

		Diary diary = diaryRepository.findById(diaryId).filter(d -> d.getPatient().equals(patient))
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXITS_DIARY));

		return DiaryResponseDto.Detail.builder()
			.title(diary.getTitle())
			.content(diary.getContent())
			.emotionState(diary.getEmotionState())
			.createdAt(diary.getCreatedAt())
			.build();
	}

	private Caregiver getCaregiverByAccessToken(String accessToken) {
		Account account = accountService.getAccountByToken(accessToken);
		return caregiverRepository.findByAccount_Id(account.getId()).orElseThrow(
			() -> new ApiException(ErrorCode.NOT_EXIST_PATIENT));
	}

	private DiaryResponseDto.Brief convertToBriefDto(Diary diary) {
		return DiaryResponseDto.Brief.builder()
			.id(diary.getId())
			.title(diary.getTitle())
			.emotionState(diary.getEmotionState())
			.createdAt(diary.getCreatedAt())
			.build();
	}

	private boolean isNotCaregiverOfPatient(Caregiver caregiver, Patient patient) {
		return caregiver.getPatientCaregiverList().stream()
			.noneMatch(pc -> pc.getPatient().equals(patient));
	}

}
