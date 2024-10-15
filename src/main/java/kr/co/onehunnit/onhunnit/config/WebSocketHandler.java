package kr.co.onehunnit.onhunnit.config;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import kr.co.onehunnit.onhunnit.domain.location.Location;
import kr.co.onehunnit.onhunnit.repository.LocationRepository;
import kr.co.onehunnit.onhunnit.service.DistrictService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {

	private final LocationRepository locationRepository;
	private final DistrictService districtService;

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		JSONParser jsonParser = new JSONParser();
		Object obj;

		try {
			obj = jsonParser.parse(message.getPayload());
		} catch (Exception e) {
			throw new IllegalArgumentException("메시지 형식이 올바르지 않습니다.");
		}

		JSONObject jsonObject = (JSONObject)obj;
		if (isNotContainAllField(jsonObject)) {
			throw new IllegalArgumentException("메시지 필드가 누락되었습니다: patient, longitude, latitude.");
		}

		Long patientId;
		double longitude;
		double latitude;
		try {
			patientId = Long.parseLong(jsonObject.get("patientId").toString());
			longitude = Double.parseDouble(jsonObject.get("longitude").toString());
			latitude = Double.parseDouble(jsonObject.get("latitude").toString());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("메시지 필드의 형식이 올바르지 않습니다.");
		}

		String responseMessage = isInside(patientId, longitude, latitude);
		session.sendMessage(new TextMessage(responseMessage));

		Location location = Location.builder()
			.latitude(latitude)
			.longitude(longitude)
			.patientId(patientId)
			.build();
		locationRepository.save(location);
	}

	boolean isNotContainAllField(JSONObject jsonObject) {
		if (jsonObject.containsKey("patientId") && jsonObject.containsKey("longitude") && jsonObject.containsKey("latitude")) {
			return false;
		}
		return true;
	}

	private String isInside(Long patientId, double longitude, double latitude) {
		boolean isInside = districtService.isPatientPointInPolygon(patientId, longitude, latitude);
		return isInside ? "현재위치가 안전 구역 내에 포함됩니다." : "현재위치가 안전 구역 내에 포함되지 않습니다.";
	}

}
