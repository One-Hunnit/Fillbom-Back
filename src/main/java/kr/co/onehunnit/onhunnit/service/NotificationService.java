package kr.co.onehunnit.onhunnit.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import kr.co.onehunnit.onhunnit.config.redis.RedisUtils;
import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.notification.Notification;
import kr.co.onehunnit.onhunnit.dto.notification.NotificationRequestDto;
import kr.co.onehunnit.onhunnit.dto.notification.NotificationRequestDto.DeviceToken;
import kr.co.onehunnit.onhunnit.dto.notification.NotificationResponseDto;
import kr.co.onehunnit.onhunnit.repository.AccountRepository;
import kr.co.onehunnit.onhunnit.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class NotificationService {

	private final WebClient.Builder webClientBuilder;
	private final AccountService accountService;
	private final NotificationRepository notificationRepository;
	private final RedisUtils redisUtils;
	private final AccountRepository accountRepository;

	private static final String EXPO_BACKEND_URI = "https://exp.host/--/api/v2/push/send";

	@Transactional
	public void pushNotification(String accessToken, NotificationRequestDto.Info infoDto) {
		Account sender = accountService.getAccountByToken(accessToken);
		Account receiver = accountRepository.findById(infoDto.getReceiverId())
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_RECEIVER_ACCOUNT));

		String deviceToken = redisUtils.getDeviceTokenByAccountID(infoDto.getReceiverId());

		sendPushNotification(deviceToken, infoDto);
		saveNotification(infoDto, sender, receiver);
	}

	public void sendPushNotification(String deviceToken, NotificationRequestDto.Info infoDto) {
		webClientBuilder.build()
			.post()
			.uri(EXPO_BACKEND_URI)
			.bodyValue(NotificationRequestDto.Push.create(deviceToken, infoDto))
			.retrieve()
			.onStatus(status -> status.isError(), clientResponse ->
				Mono.error(new RuntimeException("푸시 알림 전송에 실패하였습니다.")))
			.bodyToMono(String.class)
			.block();
	}

	public void saveNotification(NotificationRequestDto.Info infoDto, Account sender, Account receiver) {
		Notification notification = NotificationRequestDto.Info.toEntity(infoDto, sender, receiver);
		notificationRepository.save(notification);
	}

	public Slice<NotificationResponseDto> getAllNotifications(String accessToken, Pageable pageable) {
		Account receiver = accountService.getAccountByToken(accessToken);
		return notificationRepository.findAllByReceiver(receiver, pageable).map(NotificationResponseDto::from);
	}

	@Transactional
	public String saveDeviceToken(String accessToken, DeviceToken requestDto) {
		Account account = accountService.getAccountByToken(accessToken);
		return redisUtils.saveDeviceTokenInRedis(account.getId(), requestDto.getDeviceToken());
	}

	@Transactional
	public void readNotification(String accessToken, Long notificationId) {
		Account account = accountService.getAccountByToken(accessToken);
		Notification notification = notificationRepository.findById(notificationId)
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXISTS_NOTIFICATION));

		if (account.getId() != notification.getReceiver().getId()) {
			throw new ApiException(ErrorCode.UNAUTHORIZED_NOTIFICATION);
		}

		notification.read();
	}

}
