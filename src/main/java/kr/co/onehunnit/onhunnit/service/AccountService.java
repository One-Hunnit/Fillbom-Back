package kr.co.onehunnit.onhunnit.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.jsonwebtoken.JwtException;
import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import kr.co.onehunnit.onhunnit.config.jwt.JwtTokenProvider;
import kr.co.onehunnit.onhunnit.config.redis.RedisUtils;
import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.account.Provider;
import kr.co.onehunnit.onhunnit.domain.caregiver.Caregiver;
import kr.co.onehunnit.onhunnit.domain.global.Role;
import kr.co.onehunnit.onhunnit.domain.patient.Patient;
import kr.co.onehunnit.onhunnit.dto.account.AccountRequestDto;
import kr.co.onehunnit.onhunnit.dto.account.AccountResponseDto;
import kr.co.onehunnit.onhunnit.dto.account.TokenAccountInfoDto;
import kr.co.onehunnit.onhunnit.repository.AccountRepository;
import kr.co.onehunnit.onhunnit.repository.CaregiverRepository;
import kr.co.onehunnit.onhunnit.repository.NotificationRepository;
import kr.co.onehunnit.onhunnit.repository.PatientRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

	private final AccountRepository accountRepository;
	private final PatientRepository patientRepository;
	private final CaregiverRepository caregiverRepository;
	private final NotificationRepository notificationRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final LocationService locationService;
	private final RedisUtils redisUtils;

	@Transactional
	public String signUp(Account account, AccountRequestDto.SignUp requestDto) {
		accountRepository.save(account.signUp(requestDto));

		if (requestDto.getRole().equals("PATIENT")) {
			Patient patient = Patient.builder().account(account).build();
			patientRepository.save(patient);
		}
		if (requestDto.getRole().equals("CAREGIVER")) {
			Caregiver caregiver = Caregiver.builder().account(account).build();
			caregiverRepository.save(caregiver);
		}
		return requestDto.getProfileImageUrl();
	}

	@Transactional
	public AccountResponseDto.Info updateUserInfo(String accessToken, AccountRequestDto.Update updateDto) {
		Account account = getAccountByToken(accessToken);
		account.update(updateDto);
		return AccountResponseDto.Info.of(account);
	}

	@Transactional
	public void deleteAccount(String accessToken) {
		Account account = getAccountByToken(accessToken);

		if (account.getPatient() != null) {
			locationService.deletePatientLocations(account.getPatient().getId());
		}

		notificationRepository.deleteBySenderOrReceiver(account, account);
		redisUtils.deleteDeviceTokenByAccountID(account.getId());
		accountRepository.delete(account);
	}

	public AccountResponseDto.RoleInfo getAccountRole(Long accountId) {
		Account account = accountRepository.findById(accountId)
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_ACCOUNT));

		Long roleId = (account.getPatient() != null)
			? account.getPatient().getId()
			: account.getCaregiver().getId();

		return AccountResponseDto.RoleInfo.builder()
			.role(account.getRole())
			.id(roleId)
			.build();
	}

	public Account getAccountByToken(String accessToken) {
		TokenAccountInfoDto.TokenInfo tokenInfoDto = jwtTokenProvider.extractTokenInfoFromJwt(accessToken);
		String email = tokenInfoDto.getEmail();
		Provider provider = Provider.valueOf(tokenInfoDto.getProvider());

		return accountRepository.findByEmailAndProvider(email, provider)
			.orElseThrow(() -> new JwtException("토큰에 해당하는 계정 정보가 없습니다."));
	}

	public TokenAccountInfoDto getAccessTokenInfo(String accessToken) {
		Account account = getAccountByToken(accessToken);
		Role role = getRole(account);
		Integer age = calcAge(account);

		return TokenAccountInfoDto.builder()
			.account(account)
			.age(age)
			.role(role)
			.build();
	}

	private Role getRole(Account account) {
		if (patientRepository.existsByAccount_Id(account.getId())) {
			return patientRepository.findByAccount_Id(account.getId()).get();
		}
		if (caregiverRepository.existsByAccount_Id(account.getId())) {
			return caregiverRepository.findByAccount_Id(account.getId()).get();
		}
		return null;
	}

	private Integer calcAge(Account account) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
		if (account.getBirthday() == null) {
			return null;
		}
		LocalDate birthday = LocalDate.parse(account.getBirthday(), formatter);
		return Period.between(birthday, LocalDate.now()).getYears();
	}

}
