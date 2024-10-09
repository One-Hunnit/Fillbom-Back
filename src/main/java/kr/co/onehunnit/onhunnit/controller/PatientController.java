package kr.co.onehunnit.onhunnit.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.onehunnit.onhunnit.config.response.ResponseDto;
import kr.co.onehunnit.onhunnit.config.response.ResponseUtil;
import kr.co.onehunnit.onhunnit.service.PatientService;
import lombok.RequiredArgsConstructor;

@Tag(name = "환자")
@RestController
@RequiredArgsConstructor
@RequestMapping("/patient")
public class PatientController {

	private final PatientService patientService;

	@Operation(summary = "보호자 등록 요청", description = "jwt 토큰 필요")
	@PostMapping("/registration/caregivers/{caregiver_id}")
	public ResponseDto<Long> registerCaregiver(HttpServletRequest request, @PathVariable Long caregiver_id) {
		return ResponseUtil.SUCCESS("보호자 등록에 성공하였습니다.",
			patientService.registerCaregiver(request.getHeader("Authorization"), caregiver_id));
	}

}
