package kr.co.onehunnit.onhunnit.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.onehunnit.onhunnit.config.redis.RedisUtils;
import kr.co.onehunnit.onhunnit.config.response.ResponseDto;
import kr.co.onehunnit.onhunnit.config.response.ResponseUtil;
import kr.co.onehunnit.onhunnit.domain.account.AccountDetails;
import kr.co.onehunnit.onhunnit.dto.location.LocationRequestDto;
import kr.co.onehunnit.onhunnit.dto.patient.PatientRequestDto;
import kr.co.onehunnit.onhunnit.dto.patient.PatientResponseDto;
import kr.co.onehunnit.onhunnit.service.PatientService;
import lombok.RequiredArgsConstructor;

@Tag(name = "환자")
@RequiredArgsConstructor
@RestController
@RequestMapping("/patients")
public class PatientController {

	private final PatientService patientService;
	private final RedisUtils redisUtils;

	@Operation(summary = "보호자 등록 요청 수락", description = "jwt 토큰 필요, 수락은 status = ACCEPT, 거절은 status = REJECT")
	@PostMapping("/registration/caregivers/{caregiverId}/accept")
	public ResponseDto<Long> acceptCaregiverRegistration(@AuthenticationPrincipal AccountDetails accountDetails,
		@PathVariable Long caregiverId, @RequestParam String status) {
		return ResponseUtil.SUCCESS("보호자 등록 요청 수락에 성공하였습니다.",
			patientService.handleRegistration(accountDetails.getAccount(), caregiverId, status));
	}

	@Operation(summary = "전화번호로 환자 검색")
	@PostMapping("/search")
	public ResponseDto<List<PatientResponseDto.Phone>> searchByPhone(@RequestBody PatientRequestDto.Phone phoneDto) {
		return ResponseUtil.SUCCESS("환자 목록 조회에 성공하였습니다.",
			patientService.findPatientsByPhone(phoneDto.getPhoneNumber()));
	}

	@Operation(summary = "환자의 마지막 위치 저장")
	@PostMapping("/{patientId}/location")
	public ResponseDto<String> savePatientLastLocation(@PathVariable Long patientId,
		@RequestBody LocationRequestDto locationRequestDto) {
		redisUtils.saveLocationInRedis(patientId, locationRequestDto);
		return ResponseUtil.SUCCESS("환자의 마지막 위치 저장에 성공하였습니다.", null);
	}

	@Operation(summary = "환자 상세 조회")
	@GetMapping("/{patientId}")
	public ResponseDto<PatientResponseDto.Detail> getPatientDetail(
		@AuthenticationPrincipal AccountDetails accountDetails, @PathVariable Long patientId) {
		return ResponseUtil.SUCCESS("환자 상세 조회에 성공하였습니다.",
			patientService.getPatientDetail(accountDetails.getAccount(), patientId));
	}

}
