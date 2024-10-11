package kr.co.onehunnit.onhunnit.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import kr.co.onehunnit.onhunnit.domain.district.District;
import kr.co.onehunnit.onhunnit.domain.patient.Patient;
import kr.co.onehunnit.onhunnit.domain.safezone.SafeZone;
import kr.co.onehunnit.onhunnit.dto.district.DistrictResponseDto;
import kr.co.onehunnit.onhunnit.repository.DistrictRepository;
import kr.co.onehunnit.onhunnit.repository.PatientRepository;
import kr.co.onehunnit.onhunnit.repository.SafeZoneRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SafeZoneService {

	private final AccountService accountService;
	private final PatientRepository patientRepository;
	private final DistrictRepository districtRepository;
	private final SafeZoneRepository safeZoneRepository;

	public Long registerSafeZone(String accessToken, Long patientId, String adm_cd) {
		accountService.getAccountByToken(accessToken);

		Patient patient = patientRepository.findById(patientId).orElseThrow(
			() -> new ApiException(ErrorCode.NOT_EXIST_PATIENT));
		District district = districtRepository.findByAdmCd(adm_cd).orElseThrow(
			() -> new ApiException(ErrorCode.NOT_EXIST_DISTRICT));

		SafeZone safeZone = SafeZone.builder()
			.patient(patient)
			.district(district)
			.build();
		return safeZoneRepository.save(safeZone).getId();
	}

	public List<DistrictResponseDto> findAllSafeZone(Long patientId) {
		Patient patient = patientRepository.findById(patientId).orElseThrow(
			() -> new ApiException(ErrorCode.NOT_EXIST_PATIENT));
		List<SafeZone> safeZoneList = safeZoneRepository.findAllByPatientOrderByCreatedAtDesc(patient);
		return convertToDistrictResponseDtoList(safeZoneList);
	}

	private static List<DistrictResponseDto> convertToDistrictResponseDtoList(List<SafeZone> safeZoneList) {
		List<DistrictResponseDto> districtResponseDtoList = new ArrayList<>();
		for (SafeZone safeZone : safeZoneList) {
			DistrictResponseDto districtResponseDto = DistrictResponseDto.builder()
				.adm_nm(safeZone.getDistrict().getAdmNm())
				.adm_cd(safeZone.getDistrict().getAdmCd())
				.build();
			districtResponseDtoList.add(districtResponseDto);
		}
		return districtResponseDtoList;
	}

	public void deleteSafeZone(Long safeZoneId) {
		safeZoneRepository.deleteById(safeZoneId);
	}
}
