package kr.co.onehunnit.onhunnit.service;

import static kr.co.onehunnit.onhunnit.dto.diary.request.DiaryRequestDto.*;
import static kr.co.onehunnit.onhunnit.dto.diary.response.DiaryDetailResponseDto.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.account.Role;
import kr.co.onehunnit.onhunnit.domain.caregiver.Caregiver;
import kr.co.onehunnit.onhunnit.domain.diary.Diary;
import kr.co.onehunnit.onhunnit.domain.diarycontent.DiaryContent;
import kr.co.onehunnit.onhunnit.domain.diaryphoto.DiaryPhoto;
import kr.co.onehunnit.onhunnit.domain.patient.Patient;
import kr.co.onehunnit.onhunnit.domain.patient_Caregiver.PatientCaregiver;
import kr.co.onehunnit.onhunnit.dto.diary.request.DiaryRequestDto;
import kr.co.onehunnit.onhunnit.dto.diary.response.DiaryBriefResponseDto;
import kr.co.onehunnit.onhunnit.dto.diary.response.DiaryDetailResponseDto;
import kr.co.onehunnit.onhunnit.repository.CaregiverRepository;
import kr.co.onehunnit.onhunnit.repository.DiaryContentRepository;
import kr.co.onehunnit.onhunnit.repository.DiaryPhotoRepository;
import kr.co.onehunnit.onhunnit.repository.DiaryRepository;
import kr.co.onehunnit.onhunnit.repository.PatientCaregiverRepository;
import kr.co.onehunnit.onhunnit.repository.PatientRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class DiaryService {

	private final DiaryRepository diaryRepository;
	private final DiaryContentRepository diaryContentRepository;
	private final DiaryPhotoRepository diaryPhotoRepository;
	private final PatientRepository patientRepository;
	private final AccountService accountService;
	private final CaregiverRepository caregiverRepository;
	private final PatientCaregiverRepository patientCaregiverRepository;

	public Long saveDiary(String accessToken, DiaryRequestDto diaryRequestDto) {
		Account account = getAccountByAccessToken(accessToken);
		Patient patient = getPatientByAccount(account);
		Diary diary = diaryRequestDto.toEntity(diaryRequestDto, patient);
		Long diaryId = diaryRepository.save(diary).getId();

		saveDiaryContents(diaryRequestDto.getContents(), diary);

		if (diaryRequestDto.getPhotos() != null) {
			saveDiaryPhotos(diaryRequestDto.getPhotos(), diary);
		}

		return diaryId;
	}

	@Transactional(readOnly = true)
	public List<DiaryBriefResponseDto> findAllDiary(String accessToken, int month, int year) {
		Account account = getAccountByAccessToken(accessToken);

		return account.getRole().equals(Role.PATIENT)
			? findDiariesForPatient(account, month, year)
			: findDiariesForCaregiver(account, month, year);
	}

	@Transactional(readOnly = true)
	public DiaryDetailResponseDto findDiaryById(String accessToken, Long diaryId, Long patientId) {
		Account account = getAccountByAccessToken(accessToken);
		validateAccess(account, patientId);

		Diary diary = diaryRepository.findById(diaryId)
			.filter(d -> d.getPatient().getId().equals(patientId))
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_DIARY));

		if (account.getRole().equals(Role.CAREGIVER)) {
			if (!diary.isShared()) {
				throw new ApiException(ErrorCode.NOT_SHARED_DIARY);
			}
		}

		return createDiaryDetailResponse(diary);
	}

	public void deleteDiaryById(String accessToken, Long diaryId) {
		Account account = getAccountByAccessToken(accessToken);
		Patient patient = getPatientByAccount(account);
		Diary diary = diaryRepository.findById(diaryId)
			.filter(d -> d.getPatient().equals(patient))
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_DIARY));

		diaryRepository.delete(diary);
	}

	private Account getAccountByAccessToken(String accessToken) {
		return accountService.getAccountByToken(accessToken);
	}

	private Patient getPatientByAccount(Account account) {
		return patientRepository.findByAccount_Id(account.getId()).orElseThrow(
			() -> new ApiException(ErrorCode.NOT_EXIST_PATIENT));
	}

	private Caregiver getCaregiverByAccount(Account account) {
		return caregiverRepository.findByAccount_Id(account.getId()).orElseThrow(
			() -> new ApiException(ErrorCode.NOT_EXIST_CAREGIVER));
	}

	private List<Patient> getAcceptedPatients(Caregiver caregiver) {
		return patientCaregiverRepository.findAllByCaregiverAndIsAcceptedTrue(caregiver).stream()
			.map(PatientCaregiver::getPatient)
			.collect(Collectors.toList());
	}

	private void saveDiaryContents(List<DiaryContentDto> contents, Diary diary) {
		List<DiaryContent> diaryContents = contents.stream()
			.map(contentDto -> DiaryContentDto.toEntity(contentDto, diary))
			.collect(Collectors.toList());
		diaryContentRepository.saveAll(diaryContents);
	}

	private void saveDiaryPhotos(List<String> photos, Diary diary) {
		List<DiaryPhoto> diaryPhotos = photos.stream()
			.map(photoUrl -> DiaryPhoto.create(photoUrl, diary))
			.collect(Collectors.toList());
		diaryPhotoRepository.saveAll(diaryPhotos);
	}

	private List<DiaryBriefResponseDto> findDiariesForPatient(Account account, int month, int year) {
		Patient patient = getPatientByAccount(account);
		List<DiaryBriefResponseDto.Brief> briefs = diaryRepository.findAllByPatientAndDate(patient, month, year)
			.stream()
			.map(DiaryBriefResponseDto.Brief::of)
			.collect(Collectors.toList());

		return List.of(DiaryBriefResponseDto.builder()
			.patientId(patient.getId())
			.briefs(briefs)
			.build());
	}

	private List<DiaryBriefResponseDto> findDiariesForCaregiver(Account account, int month, int year) {
		Caregiver caregiver = getCaregiverByAccount(account);
		List<Patient> patients = getAcceptedPatients(caregiver);

		return patients.stream()
			.map(patient -> DiaryBriefResponseDto.builder()
				.patientId(patient.getId())
				.briefs(getDiaryBriefsForPatient(patient, month, year))
				.build())
			.collect(Collectors.toList());
	}

	private List<DiaryBriefResponseDto.Brief> getDiaryBriefsForPatient(Patient patient, int month, int year) {
		return diaryRepository.findAllByPatientAndDateAndSharedTrue(patient, month, year).stream()
			.map(DiaryBriefResponseDto.Brief::of)
			.collect(Collectors.toList());
	}

	private void validateAccess(Account account, Long patientId) {
		if (account.getRole().equals(Role.PATIENT)) {
			Patient patient = getPatientByAccount(account);
			if (!patient.getId().equals(patientId)) {
				throw new ApiException(ErrorCode.ACCESS_DENIED);
			}
		} else if (account.getRole().equals(Role.CAREGIVER)) {
			validateCaregiverAccess(account, patientId);
		}
	}

	private void validateCaregiverAccess(Account account, Long patientId) {
		Caregiver caregiver = getCaregiverByAccount(account);
		List<Patient> patients = getAcceptedPatients(caregiver);

		if (patients.stream().noneMatch(patient -> patient.getId().equals(patientId))) {
			throw new ApiException(ErrorCode.UNAUTHORIZED_ACCESS);
		}
	}

	private DiaryDetailResponseDto createDiaryDetailResponse(Diary diary) {
		List<ContentDto> contents = diary.getDiaryContents().stream()
			.map(ContentDto::of)
			.collect(Collectors.toList());

		List<String> photos = diary.getDiaryPhotos().stream()
			.map(DiaryPhoto::getPhotoUrl)
			.collect(Collectors.toList());

		return DiaryDetailResponseDto.of(diary, contents, photos);
	}

}
