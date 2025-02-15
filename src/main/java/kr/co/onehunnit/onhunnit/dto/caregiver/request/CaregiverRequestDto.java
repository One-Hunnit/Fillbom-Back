package kr.co.onehunnit.onhunnit.dto.caregiver.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CaregiverRequestDto {

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Registration {
		private String relationship;
	}

}
