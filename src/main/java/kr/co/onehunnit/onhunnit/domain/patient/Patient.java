package kr.co.onehunnit.onhunnit.domain.patient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import kr.co.onehunnit.onhunnit.domain.account.Account;
import kr.co.onehunnit.onhunnit.domain.global.BaseTimeEntity;
import kr.co.onehunnit.onhunnit.domain.patient_Caregiver.PatientCaregiver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Patient extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "patient_id")
	private Long id;

	@OneToMany(mappedBy = "patient")
	private List<PatientCaregiver> patientCaregiverList = new ArrayList<>();

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id")
	private Account account;

	public void setAccount(Account account) {
		this.account = account;
	}

}
