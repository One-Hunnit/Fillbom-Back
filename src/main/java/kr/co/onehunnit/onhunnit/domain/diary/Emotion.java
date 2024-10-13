package kr.co.onehunnit.onhunnit.domain.diary;

public enum Emotion {

	HAPPINESS("행복"),
	SADNESS("슬픔"),
	ANGER("분노"),
	ANXIETY("걱정"),
	CALMNESS("평온");

	private final String description;

	private Emotion(String description) {
		this.description = description;
	}
}
