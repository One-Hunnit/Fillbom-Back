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
		// String id = session.getId();  //메시지를 보낸 아이디
		JSONParser jsonParser = new JSONParser();
		Object obj = jsonParser.parse(message.getPayload());
		JSONObject jsonObject = (JSONObject)obj;

		Long patientId = Long.parseLong(jsonObject.get("patientId").toString());
		double longitude = Double.parseDouble(jsonObject.get("longitude").toString());
		double latitude = Double.parseDouble(jsonObject.get("latitude").toString());

		String responseMessage = isInside(patientId, longitude, latitude);
		session.sendMessage(new TextMessage(responseMessage));

		Location location = Location.builder()
			.latitude(latitude)
			.longitude(longitude)
			.patientId(patientId)
			.build();
		locationRepository.save(location);
	}

	private String isInside(Long patientId, double longitude, double latitude) {
		boolean isInside = districtService.isPatientPointInPolygon(patientId, longitude, latitude);
		return isInside ? "현재위치가 안전 구역 내에 포함됩니다." : "현재위치가 안전 구역 내에 포함되지 않습니다.";
	}

}
