package kr.co.onehunnit.onhunnit.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.account.Gender;
import kr.co.onehunnit.onhunnit.domain.account.Provider;
import kr.co.onehunnit.onhunnit.domain.account.Status;
import kr.co.onehunnit.onhunnit.domain.global.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenAccountInfoDto {

	@Schema(description = "계정 인덱스")
	private Long id;

	@Schema(description = "이메일")
	private String email;

	@Schema(description = "내용")
	private Provider provider;

	@Schema(description = "프로필 이미지 url")
	private String profile_image;

	@Schema(description = "이름")
	private String name;

	@Schema(description = "나이")
	private Integer age;

	@Schema(description = "전화번호")
	private String phone;

	@Schema(description = "성별(MAN,WOMAN)")
	private Gender gender;

	@Schema(description = "생년월일")
	private String birthday;

	@Schema(description = "상태(SIGNUP_PENDING,REGISTER_INFO_PENDING,DONE)")
	private Status status;

	@Schema(description = "역할(PATIENT,CAREGIVER")
	private String role;

	@Builder
	public TokenAccountInfoDto(Account account, Role role) {
		this.id = account.getId();
		this.email = account.getEmail();
		this.provider = account.getProvider();
		this.profile_image = account.getProfile_image();
		this.name = account.getName();
		this.age = account.getAge();
		this.phone = account.getPhone();
		this.gender = account.getGender();
		this.birthday = account.getBirthday();
		this.status = account.getStatus();
		this.role = (role == null) ? null : role.getRoleName();
	}

	@Getter
	@Builder
	public static class TokenInfo {
		private String email;
		private String provider;
	}


}
