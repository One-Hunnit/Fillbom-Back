package kr.co.onehunnit.onhunnit.domain.patient_Caregiver;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.co.onehunnit.onhunnit.domain.account.Caregiver;
import kr.co.onehunnit.onhunnit.domain.global.BaseTimeEntity;
import kr.co.onehunnit.onhunnit.domain.patient.Patient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class PatientCaregiver extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "patient_caregiver_id")
	private Long id;

	private boolean is_accepted;

	@ManyToOne
	@JoinColumn(name = "patient_id")
	private Patient patient;

	@ManyToOne
	@JoinColumn(name = "caregiver_id")
	private Caregiver caregiver;

}
