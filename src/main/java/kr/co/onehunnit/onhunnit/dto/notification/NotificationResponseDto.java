package kr.co.onehunnit.onhunnit.dto.notification;

import java.time.LocalDateTime;

import kr.co.onehunnit.onhunnit.domain.notification.Notification;
import kr.co.onehunnit.onhunnit.domain.notification.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {

	private Long id;
	private String title;
	private Type type;
	private Long senderId;
	private String senderProfileImage;
	private Long receiverId;
	private boolean isRead;
	private String body;
	private LocalDateTime createdAt;

	public static NotificationResponseDto from(Notification notification) {
		return NotificationResponseDto.builder()
			.id(notification.getId())
			.title(notification.getTitle())
			.type(notification.getType())
			.senderId(notification.getSender() != null ? notification.getSender().getId() : null)
			.senderProfileImage(notification.getSender() != null ? notification.getSender().getProfileImageUrl() : null)
			.receiverId(notification.getReceiver().getId())
			.isRead(notification.isRead())
			.body(notification.getBody())
			.createdAt(notification.getCreatedAt())
			.build();
	}

}
