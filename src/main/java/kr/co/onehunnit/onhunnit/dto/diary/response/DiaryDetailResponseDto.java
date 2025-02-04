package kr.co.onehunnit.onhunnit.dto.diary.response;

import java.time.LocalDateTime;
import java.util.List;

import kr.co.onehunnit.onhunnit.domain.diary.Diary;
import kr.co.onehunnit.onhunnit.domain.diary.Emotion;
import kr.co.onehunnit.onhunnit.domain.diarycontent.DiaryContent;
import kr.co.onehunnit.onhunnit.domain.diarycontent.Type;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DiaryDetailResponseDto {

	private Long diaryId;
	private Long patientId;
	private List<ContentDto> contents;
	private String weather;
	private Emotion emotion;
	private List<String> photos;
	private LocalDateTime createdAt;

	@Getter
	@Builder
	public static class ContentDto {
		private Type type;
		private String title;
		private String content;

		public static ContentDto of (DiaryContent diaryContent) {
			return ContentDto.builder()
				.type(diaryContent.getType())
				.title(diaryContent.getTitle())
				.content(diaryContent.getContent())
				.build();
		}

	}

	public static DiaryDetailResponseDto of (Diary diary, List<ContentDto> contents, List<String> photos) {
		return DiaryDetailResponseDto.builder()
			.diaryId(diary.getId())
			.patientId(diary.getPatient().getId())
			.contents(contents)
			.weather(diary.getWeather())
			.emotion(diary.getEmotion())
			.photos(photos)
			.createdAt(diary.getCreatedAt())
			.build();
	}

}
