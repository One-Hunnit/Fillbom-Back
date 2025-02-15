package kr.co.onehunnit.onhunnit.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.onehunnit.onhunnit.config.response.ResponseDto;
import kr.co.onehunnit.onhunnit.config.response.ResponseUtil;
import kr.co.onehunnit.onhunnit.domain.account.AccountDetails;
import kr.co.onehunnit.onhunnit.dto.caregiver.request.CaregiverRequestDto;
import kr.co.onehunnit.onhunnit.dto.caregiver.response.CaregiverResponseDto;
import kr.co.onehunnit.onhunnit.dto.patient.PatientResponseDto;
import kr.co.onehunnit.onhunnit.service.CaregiverService;
import lombok.RequiredArgsConstructor;

@Tag(name = "보호자")
@RequiredArgsConstructor
@RestController
@RequestMapping("/caregiver")
public class CaregiverController {

	private final CaregiverService caregiverService;

	@Operation(summary = "환자 등록 요청", description = "jwt 토큰 필요")
	@PostMapping("/registration/patients/{patientId}")
	public ResponseDto<CaregiverResponseDto> registerCaregiver(@AuthenticationPrincipal AccountDetails accountDetails,
		@PathVariable Long patientId,
		@RequestBody CaregiverRequestDto.Registration registration) {
		return ResponseUtil.SUCCESS("환자 등록에 성공하였습니다.",
			caregiverService.registerPatient(accountDetails.getAccount(), patientId,
				registration.getRelationship()));
	}

	@Operation(summary = "환자 목록 조회")
	@GetMapping("/patients/list")
	public ResponseDto<List<PatientResponseDto.BriefDetail>> getPatientsList(
		@AuthenticationPrincipal AccountDetails accountDetails) {
		return ResponseUtil.SUCCESS("환자 목록 조회에 성공하였습니다.",
			caregiverService.getPatientsList(accountDetails.getAccount()));
	}

}
