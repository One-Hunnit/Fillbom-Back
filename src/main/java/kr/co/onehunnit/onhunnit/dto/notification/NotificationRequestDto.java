package kr.co.onehunnit.onhunnit.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDto {

	private String to;
	private String title;
	private String body;

}
