package kr.co.onehunnit.onhunnit.dto.diary;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.onehunnit.onhunnit.domain.diary.Diary;
import kr.co.onehunnit.onhunnit.domain.diary.Emotion;
import kr.co.onehunnit.onhunnit.domain.patient.Patient;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DiaryRequestDto {
	@Schema(description = "제목")
	private String title;
	@Schema(description = "내용")
	private String content;
	@Schema(description = "감정상태(HAPPINESS,SADNESS,ANGER,ANXIETY,CALMNESS")
	private String emotionState;
	@Schema(description = "공유여부")
	private boolean shared;

	public Diary toEntity(DiaryRequestDto requestDto, Patient patient) {
		return Diary.builder()
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.emotionState(Emotion.valueOf(requestDto.getEmotionState()))
			.shared(requestDto.isShared())
			.patient(patient)
			.build();
	}
}
