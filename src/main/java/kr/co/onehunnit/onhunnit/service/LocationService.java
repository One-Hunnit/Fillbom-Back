package kr.co.onehunnit.onhunnit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.onehunnit.onhunnit.repository.LocationRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LocationService {

	private final LocationRepository locationRepository;

	public void deletePatientLocations(Long patientId) {
		locationRepository.deleteAllByPatientId(patientId);
	}

}
