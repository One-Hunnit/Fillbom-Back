package kr.co.onehunnit.onhunnit.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.onehunnit.onhunnit.config.response.ResponseDto;
import kr.co.onehunnit.onhunnit.config.response.ResponseUtil;
import kr.co.onehunnit.onhunnit.dto.district.DistrictResponseDto;
import kr.co.onehunnit.onhunnit.service.SafeZoneService;
import lombok.RequiredArgsConstructor;

@Tag(name = "안전구역")
@RestController
@RequiredArgsConstructor
@RequestMapping("/safe-zone")
public class SafeZoneController {

	private final SafeZoneService safeZoneService;

	@Operation(summary = "안전구역 설정")
	@PostMapping("/patients/{patientId}")
	public ResponseDto<Long> registerSafeZone(HttpServletRequest request, @PathVariable Long patientId,
		@RequestParam(name = "adm_cd") String adm_cd) {
		return ResponseUtil.SUCCESS("안전구역 설정에 성공하였습니다.",
			safeZoneService.registerSafeZone(request.getHeader("Authorization"), patientId, adm_cd));
	}

	@Operation(summary = "안전구역 목록 조회")
	@GetMapping("/patients/{patientId}")
	public ResponseDto<List<DistrictResponseDto>> findAllSafeZone(@PathVariable Long patientId) {
		return ResponseUtil.SUCCESS("안전구역 조회에 성공하였습니다.", safeZoneService.findAllSafeZone(patientId));
	}

	@Operation(summary = "안전구역 삭제")
	@DeleteMapping("/{safeZoneId}")
	public ResponseDto<Long> deleteSafeZone(@PathVariable Long safeZoneId) {
		safeZoneService.deleteSafeZone(safeZoneId);
		return ResponseUtil.SUCCESS("안전구역 삭제에 성공하였습니다.", safeZoneId);
	}

}
