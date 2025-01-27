package kr.co.onehunnit.onhunnit.util.patientcaregiver;

import kr.co.onehunnit.onhunnit.domain.caregiver.Caregiver;
import kr.co.onehunnit.onhunnit.domain.patient.Patient;
import kr.co.onehunnit.onhunnit.domain.patient_Caregiver.PatientCaregiver;

public class PatientCaregiverUtil {

	public static PatientCaregiver createPatientCaregiver(Patient patient, Caregiver caregiver, String relationship, boolean isAccepted) {
		return PatientCaregiver.builder()
			.patient(patient)
			.caregiver(caregiver)
			.relationship(relationship)
			.isAccepted(isAccepted)
			.build();
	}

}
