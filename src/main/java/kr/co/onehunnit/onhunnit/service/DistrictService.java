package kr.co.onehunnit.onhunnit.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import kr.co.onehunnit.onhunnit.domain.district.District;
import kr.co.onehunnit.onhunnit.domain.district.dbmapping.Feature;
import kr.co.onehunnit.onhunnit.domain.district.dbmapping.FeatureCollection;
import kr.co.onehunnit.onhunnit.domain.patient.Patient;
import kr.co.onehunnit.onhunnit.domain.safezone.SafeZone;
import kr.co.onehunnit.onhunnit.dto.district.DistrictCoordinateResponseDto;
import kr.co.onehunnit.onhunnit.dto.district.DistrictResponseDto;
import kr.co.onehunnit.onhunnit.repository.DistrictRepository;
import kr.co.onehunnit.onhunnit.repository.PatientRepository;
import kr.co.onehunnit.onhunnit.repository.SafeZoneRepository;
import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class DistrictService {

	private final AccountService accountService;
	private final DistrictRepository districtRepository;
	private final PatientRepository patientRepository;
	private final SafeZoneRepository safeZoneRepository;

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final GeometryFactory geometryFactory = new GeometryFactory();

	public void saveDistrictsFromGeoJson(String filePath) throws Exception {
		FeatureCollection featureCollection = objectMapper.readValue(new File(filePath), FeatureCollection.class);

		for (Feature feature : featureCollection.getFeatures()) {
			// 좌표 변환: GeoJSON의 MULTIPOLYGON을 WKT로 변환
			String wkt = convertToWKT(feature.getGeometry().getCoordinates());

			District districts = District.builder()
				.admNm(feature.getProperties().getAdm_nm())
				.admCd(feature.getProperties().getAdm_cd2())
				.geom((MultiPolygon)new WKTReader(geometryFactory).read(wkt))
				.build();

			districtRepository.save(districts);
		}
	}

	private String convertToWKT(double[][][][] coordinates) {
		StringBuilder wktBuilder = new StringBuilder("MULTIPOLYGON((");

		for (double[][][] polygon : coordinates) {
			wktBuilder.append("(");
			for (double[][] boundary : polygon) {
				if (boundary.length > 0) {
					for (double[] point : boundary) {
						wktBuilder.append(point[0]).append(" ").append(point[1]).append(", ");
					}
					wktBuilder.setLength(wktBuilder.length() - 2);
					wktBuilder.append(", ");
				}
			}
			wktBuilder.setLength(wktBuilder.length() - 2);
			wktBuilder.append("), ");
		}
		wktBuilder.setLength(wktBuilder.length() - 2);
		wktBuilder.append("))");

		// System.out.println("Generated WKT: " + wktBuilder.toString());
		return wktBuilder.toString();
	}

	@Transactional(readOnly = true)
	public List<DistrictResponseDto> searchDistricts(String searchWord) {
		List<District> searchedDistrictList = districtRepository.findAllByAdmNmContainingOrderById(searchWord);
		return convertoToDistrictResponseDtoList(searchedDistrictList);
	}

	private static List<DistrictResponseDto> convertoToDistrictResponseDtoList(List<District> searchedDistrictList) {
		List<DistrictResponseDto> districtResponseDtoList = new ArrayList<>();
		for (District district : searchedDistrictList) {
			DistrictResponseDto districtResponseDto = DistrictResponseDto.builder()
				.adm_cd(district.getAdmCd())
				.adm_nm(district.getAdmNm())
				.build();
			districtResponseDtoList.add(districtResponseDto);
		}
		return districtResponseDtoList;
	}

	private String convertMultiPolygonToGeoJson(MultiPolygon multiPolygon) {
		ObjectNode geoJson = objectMapper.createObjectNode();
		geoJson.put("type", "MultiPolygon");

		ArrayNode coordinates = objectMapper.createArrayNode();

		for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
			Polygon polygon = (Polygon)multiPolygon.getGeometryN(i);
			ArrayNode polygonCoordinates = objectMapper.createArrayNode();
			ArrayNode ringCoordinates = objectMapper.createArrayNode();

			for (Coordinate coordinate : polygon.getCoordinates()) {
				ArrayNode coord = objectMapper.createArrayNode();
				coord.add(coordinate.x); // 경도
				coord.add(coordinate.y); // 위도
				ringCoordinates.add(coord);
			}

			// 첫 번째 점으로 돌아가는 것을 보장하기 위해 마지막 점 추가
			if (ringCoordinates.size() > 0) {
				ArrayNode firstCoord = objectMapper.createArrayNode();
				firstCoord.add(ringCoordinates.get(0).get(0));
				firstCoord.add(ringCoordinates.get(0).get(1));
				ringCoordinates.add(firstCoord);
			}

			polygonCoordinates.add(ringCoordinates);
			coordinates.add(polygonCoordinates);
		}

		geoJson.set("coordinates", coordinates);

		return geoJson.toString();
	}

	@Transactional(readOnly = true)
	public DistrictCoordinateResponseDto getDistrictCoordinate(String admCd) {
		District district = districtRepository.findByAdmCd(admCd)
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_DISTRICT));

		return DistrictCoordinateResponseDto.builder()
			.admNm(district.getAdmNm())
			.admCd(district.getAdmCd())
			.coordinate(convertMultiPolygonToGeoJson(district.getGeom()))
			.build();
	}

	@Transactional(readOnly = true)
	public boolean isPointInPolygon(String admCd, double longitude, double latitude) {
		District district = districtRepository.findByAdmCd(admCd).orElseThrow(
			() -> new ApiException(ErrorCode.NOT_EXIST_DISTRICT));

		if (district != null && district.getGeom() != null) {
			MultiPolygon multiPolygon = district.getGeom();
			Point point = geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(longitude, latitude));
			return multiPolygon.contains(point);
		}

		throw new ApiException(ErrorCode.NOT_EXIST_DISTRICT);
	}

	// @Transactional(readOnly = true)
	public boolean isPatientPointInPolygon(Long patientId, double longitude, double latitude) {
		Patient patient = patientRepository.findById(patientId)
			.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_PATIENT));

		List<SafeZone> safeZoneList = safeZoneRepository.findAllByPatientOrderByCreatedAtDesc(patient);
		for (SafeZone safeZone : safeZoneList) {
			District district = districtRepository.findDistrictById(safeZone.getDistrict().getId())
				.orElseThrow(() -> new ApiException(ErrorCode.NOT_EXIST_DISTRICT));

			if (district != null && district.getGeom() != null) {
				MultiPolygon multiPolygon = district.getGeom();
				Point point = geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(longitude, latitude));
				if (multiPolygon.contains(point)) {
					return true;
				}
			}
		}

		return false;
	}

}
