package kr.co.onehunnit.onhunnit.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.diary.Diary;
import kr.co.onehunnit.onhunnit.domain.patient.Patient;
import kr.co.onehunnit.onhunnit.dto.diary.DiaryRequestDto;
import kr.co.onehunnit.onhunnit.dto.diary.DiaryResponseDto;
import kr.co.onehunnit.onhunnit.repository.DiaryRepository;
import kr.co.onehunnit.onhunnit.repository.PatientRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class DiaryServiceForPatient {

	private final DiaryRepository diaryRepository;
	private final PatientRepository patientRepository;

	private final AccountService accountService;

	public Long saveDiary(String accessToken, DiaryRequestDto diaryRequestDto) {
		Patient patient = getPatientByAccessToken(accessToken);
		Diary diary = diaryRequestDto.toEntity(diaryRequestDto, patient);
		return diaryRepository.save(diary).getId();
	}

	@Transactional(readOnly = true)
	public List<DiaryResponseDto.Brief> findAllDiary(String accessToken) {
		Patient patient = getPatientByAccessToken(accessToken);
		return diaryRepository.findAllByPatientOrderByCreatedAtDesc(patient).stream()
			.map(this::convertToBriefDto)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public DiaryResponseDto.Detail findDiaryById(String accessToken, Long diaryId) {
		Patient patient = getPatientByAccessToken(accessToken);
		Diary diary = diaryRepository.findById(diaryId).filter(d -> d.getPatient().equals(patient))
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXITS_DIARY));

		return DiaryResponseDto.Detail.builder()
			.title(diary.getTitle())
			.content(diary.getContent())
			.emotionState(diary.getEmotionState())
			.createdAt(diary.getCreatedAt())
			.build();
	}

	public void deleteDiaryById(String accessToken, Long diaryId) {
		Patient patient = getPatientByAccessToken(accessToken);
		Diary diary = diaryRepository.findById(diaryId)
			.filter(d -> d.getPatient().equals(patient)) // 환자와 일기 비교
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXITS_DIARY));

		diaryRepository.delete(diary);
	}

	private Patient getPatientByAccessToken(String accessToken) {
		Account account = accountService.getAccountByToken(accessToken);
		return patientRepository.findByAccount_Id(account.getId()).orElseThrow(
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

}
