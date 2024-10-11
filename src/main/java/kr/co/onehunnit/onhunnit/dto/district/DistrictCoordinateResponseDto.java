package kr.co.onehunnit.onhunnit.dto.district;

import org.locationtech.jts.geom.MultiPolygon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistrictCoordinateResponseDto {

	@Schema(description = "행정구역 명칭")
	private String admNm;

	@Schema(description = "행정구역 코드")
	private String admCd;

	@Schema(description = "행정구역 좌표")
	private String coordinate;

}
