package kr.co.onehunnit.onhunnit.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.onehunnit.onhunnit.config.response.ResponseDto;
import kr.co.onehunnit.onhunnit.config.response.ResponseUtil;
import kr.co.onehunnit.onhunnit.dto.notification.NotificationRequestDto;
import kr.co.onehunnit.onhunnit.dto.notification.NotificationResponseDto;
import kr.co.onehunnit.onhunnit.service.NotificationService;
import lombok.RequiredArgsConstructor;

@Tag(name = "알림")
@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotificationController {

	private final NotificationService notificationService;

	@Operation(summary = "알림 전송")
	@PostMapping("")
	public ResponseDto<Void> pushNotification(HttpServletRequest request,
		@RequestBody NotificationRequestDto.Info infoDto) {
		notificationService.pushNotification(request.getHeader("Authorization"), infoDto);
		return ResponseUtil.SUCCESS("알림 전송에 성공하였습니다.", null);
	}

	@Operation(summary = "알림 목록 조회")
	@GetMapping("")
	public ResponseDto<Slice<NotificationResponseDto>> getAllNotifications(HttpServletRequest request,
		@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdAt").descending());
		return ResponseUtil.SUCCESS("알림 목록 조회에 성공하였습니다.",
			notificationService.getAllNotifications(request.getHeader("Authorization"), pageable));
	}

	@Operation(summary = "알림 읽음 처리")
	@GetMapping("/{notificationId}/read")
	public ResponseDto<Void> readNotification(HttpServletRequest request, @PathVariable Long notificationId) {
		notificationService.readNotification(request.getHeader("Authorization"), notificationId);
		return ResponseUtil.SUCCESS("알림 읽음에 성공하였습니다.", null);
	}

	@Operation(summary = "디바이스 토큰 저장")
	@PostMapping("/device-token")
	public ResponseDto<String> getDeviceToken(HttpServletRequest request,
		@RequestBody NotificationRequestDto.DeviceToken requestDto) {
		return ResponseUtil.SUCCESS("디바이스 토큰 저장에 성공하였습니다.",
			notificationService.saveDeviceToken(request.getHeader("Authorization"), requestDto));
	}

}
