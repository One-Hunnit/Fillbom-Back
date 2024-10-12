package kr.co.onehunnit.onhunnit.dto.district;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistrictResponseDto {

	@Schema(description = "행정구역 명칭")
	private String adm_nm;

	@Schema(description = "행정구역 코드")
	private String adm_cd;

}
