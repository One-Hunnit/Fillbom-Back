package kr.co.onehunnit.onhunnit.domain.diary;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import kr.co.onehunnit.onhunnit.domain.diarycontent.DiaryContent;
import kr.co.onehunnit.onhunnit.domain.diaryphoto.DiaryPhoto;
import kr.co.onehunnit.onhunnit.domain.global.BaseTimeEntity;
import kr.co.onehunnit.onhunnit.domain.patient.Patient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Diary extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private Weather weather;

	@Enumerated(EnumType.STRING)
	private Emotion emotion;

	private boolean shared;

	private String audioUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_id")
	private Patient patient;

	@OneToMany(mappedBy = "diary", cascade = CascadeType.ALL)
	private List<DiaryContent> diaryContents;

	@OneToMany(mappedBy = "diary", cascade = CascadeType.ALL)
	private List<DiaryPhoto> diaryPhotos;

}
