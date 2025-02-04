package kr.co.onehunnit.onhunnit.domain.diaryphoto;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.co.onehunnit.onhunnit.domain.diary.Diary;
import kr.co.onehunnit.onhunnit.domain.global.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DiaryPhoto extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String photoUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "diary_id")
	private Diary diary;

	public static DiaryPhoto create(String photoUrl, Diary diary) {
		return DiaryPhoto.builder()
			.photoUrl(photoUrl)
			.diary(diary)
			.build();
	}
}
