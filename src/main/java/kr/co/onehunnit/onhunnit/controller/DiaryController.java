package kr.co.onehunnit.onhunnit.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.onehunnit.onhunnit.config.response.ResponseDto;
import kr.co.onehunnit.onhunnit.config.response.ResponseUtil;
import kr.co.onehunnit.onhunnit.dto.diary.request.DiaryRequestDto;
import kr.co.onehunnit.onhunnit.dto.diary.response.DiaryBriefResponseDto;
import kr.co.onehunnit.onhunnit.dto.diary.response.DiaryDetailResponseDto;
import kr.co.onehunnit.onhunnit.service.DiaryService;
import lombok.RequiredArgsConstructor;

@Tag(name = "일기")
@RestController
@RequiredArgsConstructor
@RequestMapping("/diaries")
public class DiaryController {

	private final DiaryService diaryService;

	@Operation(summary = "일기 저장")
	@PostMapping
	public ResponseDto<Long> saveDiary(HttpServletRequest request, @RequestBody DiaryRequestDto diaryRequestDto) {
		return ResponseUtil.SUCCESS("일기 저장에 성공하였습니다.",
			diaryService.saveDiary(request.getHeader("Authorization"), diaryRequestDto));
	}

	@Operation(summary = "전체 일기 목록 조회", description = "보호자의 경우 환자가 공유 허용한 일기만 조회")
	@GetMapping("/all")
	public ResponseDto<List<DiaryBriefResponseDto>> findAllDiary(HttpServletRequest request, @RequestParam int month,
		@RequestParam int year) {
		return ResponseUtil.SUCCESS("전체 목록 조회에 성공하였습니다.",
			diaryService.findAllDiary(request.getHeader("Authorization"), month, year));
	}

	@Operation(summary = "일기 상세 조회")
	@GetMapping("/{diaryId}/patients/{patientId}")
	public ResponseDto<DiaryDetailResponseDto> findDiaryById(HttpServletRequest request, @PathVariable Long diaryId, @PathVariable Long patientId) {
		return ResponseUtil.SUCCESS("일기 조회에 성공하였습니다.",
			diaryService.findDiaryById(request.getHeader("Authorization"), diaryId, patientId));
	}

	@Operation(summary = "일기 삭제")
	@DeleteMapping("/{diaryId}")
	public ResponseDto<Void> deleteDiaryById(HttpServletRequest request, @PathVariable Long diaryId) {
		diaryService.deleteDiaryById(request.getHeader("Authorization"), diaryId);
		return ResponseUtil.SUCCESS("일기 삭제에 성공하였습니다.", null);
	}

}
