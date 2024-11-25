package kr.co.onehunnit.onhunnit.dto.token;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdTokenDto {
	@NotNull(message = "id_token은 필수입니다.")
	private String idToken;
}
