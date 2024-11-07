package kr.co.onehunnit.onhunnit.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/notification")
public class NotificationController {

	private final NotificationService notificationService;

	@Operation(summary = "알림 전송")
	@PostMapping("")
	public ResponseDto<String> pushNotification(HttpServletRequest request, @RequestBody NotificationRequestDto notificationRequestDto) {
		return ResponseUtil.SUCCESS("알림 전송에 성공하였습니다.", notificationService.pushNotification(request.getHeader("Authorization"), notificationRequestDto));
	}

	@Operation(summary = "알림 목록 조회")
	@GetMapping("/all")
	public ResponseDto<List<NotificationResponseDto>> getAllNotifications(HttpServletRequest request) {
		return ResponseUtil.SUCCESS("알림 목록 조회에 성공하였습니다.", notificationService.getAllNotifications(request.getHeader("Authorization")));
	}

	@Operation(summary = "알림 조회")
	@GetMapping("/{notificationId}")
	public ResponseDto<NotificationResponseDto> getNotification(HttpServletRequest request, @PathVariable Long notificationId) {
		return ResponseUtil.SUCCESS("알림 조회에 성공하였습니다.", notificationService.findNotificationById(request.getHeader("Authorization"), notificationId));
	}

}
