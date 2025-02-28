package kr.co.onehunnit.onhunnit.dto.caregiver.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CaregiverResponseDto {

	Long patientCaregiverId;

	public static CaregiverResponseDto of(Long patientCaregiverId) {
		return CaregiverResponseDto.builder()
			.patientCaregiverId(patientCaregiverId)
			.build();
	}

	@Getter
	@Builder
	public static class Id {
		Long caregiverId;

		public static Id of(Long caregiverId) {
			return Id.builder()
				.caregiverId(caregiverId)
				.build();
		}
	}

}
