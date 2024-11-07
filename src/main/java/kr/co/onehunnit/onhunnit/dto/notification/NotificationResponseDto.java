package kr.co.onehunnit.onhunnit.dto.notification;

import java.time.LocalDateTime;

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
	private String body;
	private LocalDateTime createdAt;

}
