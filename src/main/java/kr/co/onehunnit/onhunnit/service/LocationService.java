package kr.co.onehunnit.onhunnit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.onehunnit.onhunnit.config.redis.RedisUtils;
import kr.co.onehunnit.onhunnit.dto.location.LocationRequestDto;
import kr.co.onehunnit.onhunnit.repository.LocationRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LocationService {

	private final RedisUtils redisUtils;
	private final ObjectMapper objectMapper;
	private final LocationRepository locationRepository;

	public void deletePatientLocations(Long patientId) {
		locationRepository.deleteAllByPatientId(patientId);
	}

}
