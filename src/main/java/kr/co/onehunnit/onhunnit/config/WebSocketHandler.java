package kr.co.onehunnit.onhunnit.config;

import java.io.IOException;

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
	private final JSONParser jsonParser = new JSONParser();

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		JSONObject jsonObject = parseMessage(session, message.getPayload());
		if (jsonObject == null) {
			return;
		}

		if (!isContainAllField(jsonObject)) {
			session.sendMessage(new TextMessage("메시지 필드가 누락되었습니다: patientId, longitude, latitude"));
			return;
		}

		Long patientId;
		double longitude;
		double latitude;
		try {
			patientId = Long.parseLong(jsonObject.get("patientId").toString());
			longitude = Double.parseDouble(jsonObject.get("longitude").toString());
			latitude = Double.parseDouble(jsonObject.get("latitude").toString());
		} catch (NumberFormatException e) {
			session.sendMessage(new TextMessage("메시지 필드의 형식이 올바르지 않습니다."));
			return;
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

	private JSONObject parseMessage(WebSocketSession session, String payload) throws IOException {
		try {
			return (JSONObject)jsonParser.parse(payload);
		} catch (Exception e) {
			session.sendMessage(new TextMessage("메시지 형식이 올바르지 않습니다."));
			return null;
		}
	}

	boolean isContainAllField(JSONObject jsonObject) {
		return jsonObject.containsKey("patientId") && jsonObject.containsKey("longitude") && jsonObject.containsKey(
			"latitude");
	}

	private String isInside(Long patientId, double longitude, double latitude) {
		boolean isInside = districtService.isPatientPointInPolygon(patientId, longitude, latitude);
		return isInside ? "현재위치가 안전 구역 내에 포함됩니다." : "현재위치가 안전 구역 내에 포함되지 않습니다.";
	}

}
