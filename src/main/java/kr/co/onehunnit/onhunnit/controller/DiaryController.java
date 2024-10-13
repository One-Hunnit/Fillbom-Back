package kr.co.onehunnit.onhunnit.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.onehunnit.onhunnit.config.response.ResponseDto;
import kr.co.onehunnit.onhunnit.config.response.ResponseUtil;
import kr.co.onehunnit.onhunnit.dto.diary.DiaryRequestDto;
import kr.co.onehunnit.onhunnit.dto.diary.DiaryResponseDto;
import kr.co.onehunnit.onhunnit.service.DiaryServiceForCaregiver;
import kr.co.onehunnit.onhunnit.service.DiaryServiceForPatient;
import lombok.RequiredArgsConstructor;

@Tag(name = "일기")
@RestController
@RequiredArgsConstructor
@RequestMapping("/diaries")
public class DiaryController {

	private final DiaryServiceForPatient diaryServiceForPatient;
	private final DiaryServiceForCaregiver diaryServiceForCaregiver;

	@Operation(summary = "일기 저장")
	@PostMapping("")
	public ResponseDto<Long> saveDiary(HttpServletRequest request, @RequestBody DiaryRequestDto diaryRequestDto) {
		return ResponseUtil.SUCCESS("일기 저장에 성공하였습니다.",
			diaryServiceForPatient.saveDiary(request.getHeader("Authorization"), diaryRequestDto));
	}

	@Operation(summary = "환자가 전체 일기 목록 조회")
	@GetMapping("/all")
	public ResponseDto<List<DiaryResponseDto.Brief>> findAllDiary(HttpServletRequest request) {
		return ResponseUtil.SUCCESS("전체 목록 조회에 성공하였습니다.",
			diaryServiceForPatient.findAllDiary(request.getHeader("Authorization")));
	}

	@Operation(summary = "환자가 일기 조회")
	@GetMapping("/{diaryId}")
	public ResponseDto<DiaryResponseDto.Detail> findDiaryById(HttpServletRequest request, @PathVariable Long diaryId) {
		return ResponseUtil.SUCCESS("일기 조회에 성공하였습니다.",
			diaryServiceForPatient.findDiaryById(request.getHeader("Authorization"), diaryId));
	}

	@Operation(summary = "환자가 일기 삭제")
	@DeleteMapping("/{diaryId}")
	public String deleteDiaryById(HttpServletRequest request, @PathVariable Long diaryId) {
		diaryServiceForPatient.deleteDiaryById(request.getHeader("Authorization"), diaryId);
		return "일기 삭제에 성공하였습니다.";
	}

	@Operation(summary = "보호자가 환자의 일기 목록 조회", description = "환자가 공유 허용한 일기만 조회")
	@GetMapping("/patients/{patientId}/caregivers")
	public ResponseDto<List<DiaryResponseDto.Brief>> findPatientDiariesForCaregivers(HttpServletRequest request,
		@PathVariable Long patientId) {
		return ResponseUtil.SUCCESS("환자의 일기 목록 조회에 성공하였습니다.",
			diaryServiceForCaregiver.findPatientDiariesForCaregiver(request.getHeader("Authorization"), patientId));
	}

	@Operation(summary = "보호자가 환자의 일기 조회", description = "환자가 공유 허용한 일기만 조회")
	@GetMapping("{diaryId}/patients/{patientId}/caregivers")
	public ResponseDto<DiaryResponseDto.Detail> findPatientDiaryForCaregivers(HttpServletRequest request,
		@PathVariable Long patientId, @PathVariable Long diaryId) {
		return ResponseUtil.SUCCESS("환자의 일기 조회에 성공하였습니다.",
			diaryServiceForCaregiver.findPatientDiaryForCaregiver(request.getHeader("Authorization"), patientId, diaryId));
	}

}
