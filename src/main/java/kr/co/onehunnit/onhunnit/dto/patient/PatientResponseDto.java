package kr.co.onehunnit.onhunnit.dto.patient;

import kr.co.onehunnit.onhunnit.domain.account.Gender;
import lombok.Builder;
import lombok.Getter;

public class PatientResponseDto {

	@Getter
	@Builder
	public static class BriefDetail {
		private String profileImageUrl;
		private String name;
		private String relationship;
		private boolean isAccepted;
	}

	@Getter
	@Builder
	public static class Phone {
		private String profileImageUrl;
		private String name;
		private String phoneNumber;
	}

	@Getter
	@Builder
	public static class Detail {
		private String profileImageUrl;
		private String name;
		private Gender gender;
		private String birthday;
		private String phoneNumber;
		private Location location;
	}

	@Getter
	@Builder
	public static class Location {
		private String latitude;
		private String longitude;
	}

}
