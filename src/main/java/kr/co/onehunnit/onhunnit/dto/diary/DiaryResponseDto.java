package kr.co.onehunnit.onhunnit.dto.diary;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.onehunnit.onhunnit.domain.diary.Emotion;
import lombok.Builder;
import lombok.Getter;

public class DiaryResponseDto {

	@Getter
	@Builder
	public static class Brief{
		@Schema(description = "일기 인덱스")
		private Long id;
		@Schema(description = "제목")
		private String title;
		@Schema(description = "감정상태(HAPPINESS,SADNESS,ANGER,ANXIETY,CALMNESS")
		private Emotion emotionState;
		@Schema(description = "생성일자")
		private LocalDateTime createdAt;
	}

	@Getter
	@Builder
	public static class Detail {
		@Schema(description = "제목")
		private String title;
		@Schema(description = "내용")
		private String content;
		@Schema(description = "감정상태(HAPPINESS,SADNESS,ANGER,ANXIETY,CALMNESS")
		private Emotion emotionState;
		@Schema(description = "생성일자")
		private LocalDateTime createdAt;
	}

}
