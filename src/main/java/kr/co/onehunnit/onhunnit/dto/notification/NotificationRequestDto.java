package kr.co.onehunnit.onhunnit.dto.notification;

import static kr.co.onehunnit.onhunnit.domain.notification.Type.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.notification.Notification;
import kr.co.onehunnit.onhunnit.domain.notification.Type;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class NotificationRequestDto {

	@Getter
	@Builder
	public static class DeviceToken {
		private String deviceToken;

		@JsonCreator
		public DeviceToken(String deviceToken) {
			this.deviceToken = deviceToken;
		}
	}

	@Getter
	@Builder
	public static class Info {
		private String title;
		private Type type;
		private Long senderId;
		private Long receiverId;
		private String body;

		public static Notification toEntity(Info info, Account sender, Account receiver) {
			return Notification.builder()
				.type(info.getType())
				.title(info.getTitle())
				.body(info.getBody())
				.sender((info.getType() == RELEASED || info.getType() == ETC) ? null : sender)
				.receiver(receiver)
				.isRead(false)
				.build();
		}

	}

	@Getter
	@Builder
	public static class Push {
		private String to;
		private String title;
		private String body;

		public static Push create(String deviceToken, Info info) {
			return Push.builder()
				.to(deviceToken)
				.title(info.getTitle())
				.body(info.getBody())
				.build();
		}
	}

}
