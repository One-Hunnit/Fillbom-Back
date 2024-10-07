package kr.co.onehunnit.onhunnit.dto.account;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenAccountInfoDto {

	private String email;
	private String provider;

}
