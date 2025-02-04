package kr.co.onehunnit.onhunnit.dto.diary.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.onehunnit.onhunnit.domain.diary.Diary;
import kr.co.onehunnit.onhunnit.domain.diary.Emotion;
import kr.co.onehunnit.onhunnit.domain.diarycontent.DiaryContent;
import kr.co.onehunnit.onhunnit.domain.diarycontent.Type;
import kr.co.onehunnit.onhunnit.domain.patient.Patient;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DiaryRequestDto {
	@Schema(description = "내용 목록")
	private List<DiaryContentDto> contents;

	@Schema(description = "날씨")
	private String weather;

	@Schema(description = "감정상태(HAPPINESS,SADNESS,ANGER,ANXIETY,CALMNESS")
	private String emotion;

	@Schema(description = "오디오URL")
	private String audioUrl;

	@Schema(description = "사진 URL 목록")
	private List<String> photos;

	@Schema(description = "공유여부")
	private boolean shared;

	@Getter
	@Builder
	public static class DiaryContentDto {
		@Schema(description = "일기 타입(QNA, FREE)")
		private Type type;

		@Schema(description = "제목")
		private String title;

		@Schema(description = "내용")
		private String content;

		public static DiaryContent toEntity(DiaryContentDto contentDto, Diary diary) {
			return DiaryContent.builder()
				.type(contentDto.type)
				.title(contentDto.title)
				.content(contentDto.content)
				.diary(diary)
				.build();
		}

	}

	public Diary toEntity(DiaryRequestDto requestDto, Patient patient) {
		return Diary.builder()
			.weather(requestDto.getWeather())
			.emotion(Emotion.valueOf(requestDto.getEmotion()))
			.shared(requestDto.isShared())
			.audioUrl(requestDto.getAudioUrl())
			.patient(patient)
			.build();
	}

}
