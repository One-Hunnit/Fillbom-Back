package kr.co.onehunnit.onhunnit.dto.diary.response;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.onehunnit.onhunnit.domain.diary.Diary;
import kr.co.onehunnit.onhunnit.domain.diary.Emotion;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DiaryBriefResponseDto {

	private Long patientId;
	private List<Brief> briefs;

	@Getter
	@Builder
	public static class Brief{
		@Schema(description = "일기 인덱스")
		private Long diaryId;
		@Schema(description = "감정상태(HAPPINESS,SADNESS,ANGER,ANXIETY,CALMNESS")
		private Emotion emotion;
		@Schema(description = "생성일자")
		private LocalDateTime createdAt;

		public static Brief of (Diary diary) {
			return Brief.builder()
				.diaryId(diary.getId())
				.emotion(diary.getEmotion())
				.createdAt(diary.getCreatedAt())
				.build();
		}
	}

}
