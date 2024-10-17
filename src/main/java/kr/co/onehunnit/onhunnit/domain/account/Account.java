package kr.co.onehunnit.onhunnit.domain.account;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import kr.co.onehunnit.onhunnit.domain.global.BaseTimeEntity;
import kr.co.onehunnit.onhunnit.domain.patient.Patient;
import kr.co.onehunnit.onhunnit.dto.account.AccountRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String email;

	@Enumerated(value = EnumType.STRING)
	private Provider provider;

	private String profile_image;

	private String name;

	private String phone;

	@Enumerated(value = EnumType.STRING)
	private Gender gender;

	private String birthday;

	@OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
	private Patient patient;

	@OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
	private Caregiver caregiver;

	public void signUp(AccountRequestDto.SignUp requestDto) {
		this.name = requestDto.getName();
		this.phone = requestDto.getPhone();
		this.gender = Gender.valueOf(requestDto.getGender());
		this.birthday = requestDto.getBirthday();
		this.profile_image = requestDto.getProfile_image();
	}

	public void update(AccountRequestDto.Update updateDto) {
		this.name = updateDto.getName();
		this.birthday = updateDto.getBirthday();
		this.profile_image = updateDto.getProfile_image();
		this.phone = updateDto.getPhone();
		this.gender = updateDto.getGender();
	}

}
