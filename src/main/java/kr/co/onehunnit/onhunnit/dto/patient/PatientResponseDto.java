package kr.co.onehunnit.onhunnit.dto.patient;

import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.account.Gender;
import kr.co.onehunnit.onhunnit.domain.patient_Caregiver.PatientCaregiver;
import lombok.Builder;
import lombok.Getter;

public class PatientResponseDto {

	@Getter
	@Builder
	public static class BriefDetail {
		private Long patientId;
		private String profileImageUrl;
		private String name;
		private String relationship;
		private boolean isAccepted;

		public static BriefDetail of(Account account, PatientCaregiver patientCaregiver) {
			return BriefDetail.builder()
				.patientId(patientCaregiver.getPatient().getId())
				.profileImageUrl(account.getProfileImageUrl())
				.name(account.getName())
				.relationship(patientCaregiver.getRelationship())
				.isAccepted(patientCaregiver.isAccepted())
				.build();
		}
	}

	@Getter
	@Builder
	public static class Phone {
		private Long patientId;
		private String profileImageUrl;
		private String name;
		private String phoneNumber;

		public static Phone of(Account account) {
			return Phone.builder()
				.patientId(account.getPatient().getId())
				.name(account.getName())
				.phoneNumber(account.getPhone())
				.profileImageUrl(account.getProfileImageUrl())
				.build();
		}
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

		public static Detail of(Account account, Location location) {
			return Detail.builder()
				.profileImageUrl(account.getProfileImageUrl())
				.name(account.getName())
				.gender(account.getGender())
				.birthday(account.getBirthday())
				.phoneNumber(account.getPhone())
				.location(location)
				.build();
		}
	}

	@Getter
	@Builder
	public static class Location {
		private String latitude;
		private String longitude;
	}

}
