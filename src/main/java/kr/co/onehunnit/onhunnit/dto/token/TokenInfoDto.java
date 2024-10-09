package kr.co.onehunnit.onhunnit.dto.token;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
@Builder
public class TokenInfoDto {

	@Schema(description = "허가 타입(Bearer)")
	private final String grantType;
	@Schema(description = "액세스 토큰")
	private final String accessToken;
	@Schema(description = "리프레시 토큰")
	private final String refreshToken;

}
