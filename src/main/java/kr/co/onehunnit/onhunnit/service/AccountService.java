package kr.co.onehunnit.onhunnit.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import kr.co.onehunnit.onhunnit.config.jwt.JwtTokenProvider;
import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.account.Gender;
import kr.co.onehunnit.onhunnit.domain.account.Provider;
import kr.co.onehunnit.onhunnit.dto.account.AccountRequestDto;
import kr.co.onehunnit.onhunnit.dto.account.TokenAccountInfoDto;
import kr.co.onehunnit.onhunnit.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

	private final AccountRepository accountRepository;
	private final JwtTokenProvider jwtTokenProvider;

	//ToDo 회원가입 명세서에 맞게 수정
	public Long signUp(AccountRequestDto.SignUp requestDto) {
		String email = requestDto.getEmail();
		Provider provider = Provider.valueOf(requestDto.getProvider());
		Account account = accountRepository.findByEmailAndProvider(email, provider)
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_EMAIL));

		account.signUp(requestDto);
		return account.getId();
	}

	public Account getAccountByToken(String accessToken) {
		TokenAccountInfoDto tokenAccountInfoDto = jwtTokenProvider.extractTokenAccountInfoFromJwt(accessToken);
		String email = tokenAccountInfoDto.getEmail();
		Provider provider = Provider.valueOf(tokenAccountInfoDto.getProvider());
		return accountRepository.findByEmailAndProvider(email, provider)
			.orElseThrow(() -> new ApiException(ErrorCode.NO_TOKEN_ACCOUNT));
	}

	public TokenAccountInfoDto getAccessTokenInfo(String accessToken) {
		return jwtTokenProvider.extractTokenAccountInfoFromJwt(accessToken);
	}

}
