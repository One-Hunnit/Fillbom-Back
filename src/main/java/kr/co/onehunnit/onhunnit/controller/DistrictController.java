package kr.co.onehunnit.onhunnit.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.onehunnit.onhunnit.config.response.ResponseDto;
import kr.co.onehunnit.onhunnit.config.response.ResponseUtil;
import kr.co.onehunnit.onhunnit.dto.district.DistrictCoordinateResponseDto;
import kr.co.onehunnit.onhunnit.dto.district.DistrictResponseDto;
import kr.co.onehunnit.onhunnit.service.DistrictService;
import lombok.RequiredArgsConstructor;

@Tag(name = "행정구역")
@RestController
@RequiredArgsConstructor
@RequestMapping("/district")
public class DistrictController {

	private final DistrictService districtService;

	@Operation(summary = "행정구역 데이터 저장", description = "로컬에서 데이터 저장 용도, 배포 서버 사용X")
	@GetMapping("/save-data")
	public void processGeoJson() {
		try {
			districtService.saveDistrictsFromGeoJson(
				"C:/Users/KoKyungNam/Desktop/fillbom/HangJeongDong_ver20241001.geojson");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Operation(summary = "행정 구역 검색", description = "jwt 토큰 필요")
	@GetMapping("/search")
	public ResponseDto<List<DistrictResponseDto>> searchDistricts(@RequestParam String searchWord) {
		return ResponseUtil.SUCCESS("검색에 성공하였습니다.", districtService.searchDistricts(searchWord));
	}

	@Operation(summary = "행정 구역 정보 조회", description = "jwt 토큰 필요, 좌표 제공")
	@GetMapping("/coordinate")
	public ResponseDto<DistrictCoordinateResponseDto> getDistrictCoordinate(@RequestParam String adm_cd) {
		return ResponseUtil.SUCCESS("행정구역 조회에 성공하였습니다.", districtService.getDistrictCoordinate(adm_cd));
	}

	@Operation(summary = "행정구역 내에 포함 여부 확인", description = "jwt 토큰 필요")
	@GetMapping("/check/location/safe-zone")
	public String checkLocation(@RequestParam String adm_cd, @RequestParam double longitude, @RequestParam double latitude) {
		boolean isInside = districtService.isPointInPolygon(adm_cd, longitude, latitude);
		return isInside ? "현재위치가 행정구역 내에 포함됩니다." : "현재위치가 행정구역 내에 포함되지 않습니다.";
	}

}
