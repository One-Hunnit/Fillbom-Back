package kr.co.onehunnit.onhunnit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.dto.account.AccountRequestDto;
import kr.co.onehunnit.onhunnit.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

	private final AccountRepository accountRepository;

	//ToDo 회원가입 명세서에 맞게 수정
	public Long signUp(AccountRequestDto.SignUp requestDto) {
		if (accountRepository.existsByEmail(requestDto.getEmail())) {
			throw new ApiException(ErrorCode.ACCOUNT_DUPLICATED_ERROR);
		}

		Account account = Account.builder()
			.email(requestDto.getEmail())
			.name(requestDto.getName())
			.phone(requestDto.getPhoneNumber())
			.build();

		return accountRepository.save(account).getId();
	}
}
