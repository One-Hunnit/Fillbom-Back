package kr.co.onehunnit.onhunnit.config.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import kr.co.onehunnit.onhunnit.dto.location.LocationRequestDto;
import kr.co.onehunnit.onhunnit.dto.patient.PatientResponseDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RedisUtils {

	private final RedisTemplate<String, Object> redisTemplate;

	public void saveLocationInRedis(Long patient, LocationRequestDto locationRequestDto) {
		String latKey = "patient:location:" + patient + ":latitude";
		String lonKey = "patient:location:" + patient + ":longitude";

		redisTemplate.opsForValue().set(latKey, locationRequestDto.getLatitude());
		redisTemplate.opsForValue().set(lonKey, locationRequestDto.getLongitude());
	}

	public PatientResponseDto.Location getLocationByPatientId(Long patientId) {
		String latKey = "patient:location:" + patientId + ":latitude";
		String lonKey = "patient:location:" + patientId + ":longitude";

		String latitude = (String)redisTemplate.opsForValue().get(latKey);
		String longitude = (String)redisTemplate.opsForValue().get(lonKey);

		if (latitude == null || longitude == null) {
			return null;
		}

		return PatientResponseDto.Location.builder()
			.latitude(latitude)
			.longitude(longitude)
			.build();
	}

	public void deleteLocationByPatientId(Long patientId) {
		String latKey = "patient:location:" + patientId + ":latitude";
		String lonKey = "patient:location:" + patientId + ":longitude";

		redisTemplate.delete(latKey);
		redisTemplate.delete(lonKey);
	}

	public String saveDeviceTokenInRedis(Long accountId, String deviceToken) {
		String key = accountId + "'s deviceToken";
		redisTemplate.opsForValue().set(key, deviceToken);
		return key;
	}

	public String getDeviceTokenByAccountID(Long accountId) {
		String key = accountId + "'s deviceToken";
		String deviceToken = (String) redisTemplate.opsForValue().get(key);

		return deviceToken;
	}

	public void deleteDeviceTokenByAccountID(Long accountId) {
		String key = accountId + "'s deviceToken";
		redisTemplate.delete(key);
	}

}
