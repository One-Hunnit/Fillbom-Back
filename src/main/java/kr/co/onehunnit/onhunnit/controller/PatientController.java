package kr.co.onehunnit.onhunnit.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.onehunnit.onhunnit.config.response.ResponseDto;
import kr.co.onehunnit.onhunnit.config.response.ResponseUtil;
import kr.co.onehunnit.onhunnit.service.PatientService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patient")
public class PatientController {

	private final PatientService patientService;

	@PostMapping("/registration/caregivers/{caregiver_id}")
	public ResponseDto<Long> registerCaregiver(HttpServletRequest request, @PathVariable Long caregiver_id) {
		return ResponseUtil.SUCCESS("보호자 등록에 성공하였습니다.",
			patientService.registerCaregiver(request.getHeader("Authorization"), caregiver_id));
	}

}
