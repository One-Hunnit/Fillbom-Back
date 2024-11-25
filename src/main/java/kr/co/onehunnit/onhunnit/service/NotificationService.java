package kr.co.onehunnit.onhunnit.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.notification.Notification;
import kr.co.onehunnit.onhunnit.dto.notification.NotificationRequestDto;
import kr.co.onehunnit.onhunnit.dto.notification.NotificationResponseDto;
import kr.co.onehunnit.onhunnit.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class NotificationService {

	private final WebClient.Builder webClientBuilder;
	private final AccountService accountService;
	private final NotificationRepository notificationRepository;

	private static final String EXPO_BACKEND_URI = "https://exp.host/--/api/v2/push/send";

	public String pushNotification(String accessToken, NotificationRequestDto notificationRequestDto) {
		Account account = accountService.getAccountByToken(accessToken);
		WebClient webClient = webClientBuilder.build();

		Mono<String> response = webClient.post()
			.uri(EXPO_BACKEND_URI)
			.bodyValue(notificationRequestDto)
			.retrieve()
			.onStatus(status -> status.isError(), clientResponse -> {
				return Mono.error(new RuntimeException("푸시 알림 전송에 실패하였습니다."));
			})
			.bodyToMono(String.class)
			.doOnSuccess(res -> {
				Notification notification = Notification.builder()
					.title(notificationRequestDto.getTitle())
					.body(notificationRequestDto.getBody())
					.account(account)
					.build();
				notificationRepository.save(notification);
			});

		return response.block();
	}

	public List<NotificationResponseDto> getAllNotifications(String accessToken) {
		Account account = accountService.getAccountByToken(accessToken);
		List<Notification> notificationList = notificationRepository.findAllByAccountIdOrderByCreatedAtDesc(
			account.getId());

		return notificationList.stream()
			.map(this::convertToDto)
			.collect(Collectors.toList());
	}

	private NotificationResponseDto convertToDto(Notification notification) {
		return NotificationResponseDto.builder()
			.id(notification.getId())
			.title(notification.getTitle())
			.body(notification.getBody())
			.createdAt(notification.getCreatedAt())
			.build();
	}

	public NotificationResponseDto findNotificationById(String accessToken, Long notificationId) {
		Account account = accountService.getAccountByToken(accessToken);
		Notification notification = notificationRepository.findById(notificationId)
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_NOTIFICATION));

		if (account.getId() != notification.getAccount().getId()) {
			throw new ApiException(ErrorCode.NOT_EXISTS_ACCOUNT_NOTIFICATION);
		}

		return NotificationResponseDto.builder()
			.id(notification.getId())
			.title(notification.getTitle())
			.body(notification.getBody())
			.createdAt(notification.getCreatedAt())
			.build();
	}
}
