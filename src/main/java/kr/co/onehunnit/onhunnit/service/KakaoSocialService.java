package kr.co.onehunnit.onhunnit.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kr.co.onehunnit.onhunnit.domain.account.web.KakaoProperties;
import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class KakaoSocialService {

	private static final String KAKAO_LOGIN_REQUEST_URL = "https://kauth.kakao.com/oauth/token";
	private static final String KAKAO_USER_INFO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";

	private final KakaoProperties kakaoProperties;

	public String getKakaoAccessToken(String authorizationCode) {
		try {
			HttpURLConnection kakaoServerConnection = getKakaoServerConnectionForAuthorizationCode();
			sendAuthorizationCodeToKakaoServer(kakaoServerConnection, authorizationCode);

			if (kakaoServerConnection.getResponseCode() == 200) {
				return getKakaoAccessToken(kakaoServerConnection);
			} else {
				throw new ApiException(ErrorCode.RESPONSE_CODE_ERROR);
			}
		} catch (IOException e) {
			throw new ApiException(ErrorCode.FAILED_TO_RETRIEVE_KAKAO_ACCESS_TOKEN);
		}
	}

	public HashMap<String, Object> getKakaoUserInfo(String accessToken) {
		try {
			HttpURLConnection kakaoServerConnection = getKakaoServerConnectionForUserInfo(accessToken);

			if (kakaoServerConnection.getResponseCode() == 200) {
				return getKakaoUserInfo(kakaoServerConnection);
			} else {
				throw new ApiException(ErrorCode.RESPONSE_CODE_ERROR);
			}
		} catch (IOException e) {
			throw new ApiException(ErrorCode.FAILED_TO_RETRIEVE_KAKAO_USER_INFO);
		}
	}

	private static HttpURLConnection getKakaoServerConnectionForUserInfo(String accessToken) throws IOException {
		URL url = new URL(KAKAO_USER_INFO_REQUEST_URL);
		HttpURLConnection kakaoServerConnection = (HttpURLConnection)url.openConnection();
		kakaoServerConnection.setRequestMethod("POST");
		kakaoServerConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
		return kakaoServerConnection;
	}

	private static HttpURLConnection getKakaoServerConnectionForAuthorizationCode() throws IOException {
		URL kakaoLoginRequestUrl = new URL(KAKAO_LOGIN_REQUEST_URL);
		HttpURLConnection kakaoServerConnection = (HttpURLConnection)kakaoLoginRequestUrl.openConnection();
		kakaoServerConnection.setRequestMethod("POST");
		kakaoServerConnection.setDoOutput(true);
		return kakaoServerConnection;
	}

	private void sendAuthorizationCodeToKakaoServer(HttpURLConnection kakaoServerConnection,
		String authorizationCode) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(kakaoServerConnection.getOutputStream()));

		StringBuilder requestBody = new StringBuilder();
		requestBody.append("grant_type=authorization_code");
		requestBody.append("&client_id=" + kakaoProperties.getClientId());
		requestBody.append("&client_secret=" + kakaoProperties.getClientSecret());
		requestBody.append("&redirect_uri=" + kakaoProperties.getRedirectUri());
		requestBody.append("&code=" + authorizationCode);

		bw.write(requestBody.toString());
		bw.flush();
		bw.close();
	}

	private HashMap<String, Object> getKakaoUserInfo(HttpURLConnection kakaoServerConnection) throws IOException {
		String responseBody = readResponse(kakaoServerConnection);

		JsonElement jsonElement = JsonParser.parseString(responseBody);
		JsonObject properties = jsonElement.getAsJsonObject().get("properties").getAsJsonObject();
		JsonObject kakaoAccount = jsonElement.getAsJsonObject().get("kakao_account").getAsJsonObject();

		String nickname = properties.getAsJsonObject().get("nickname").getAsString();
		// String email = kakaoAccount.getAsJsonObject().get("email").getAsString();

		HashMap<String, Object> kakaoUserInfo = new HashMap<>();
		kakaoUserInfo.put("nickname", nickname);
		// kakaoUserInfo.put("email", email);

		return kakaoUserInfo;
	}

	private String getKakaoAccessToken(HttpURLConnection kakaoServerConnection) throws IOException {
		String responseBody = readResponse(kakaoServerConnection);
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(responseBody);
		return jsonElement.getAsJsonObject().get("access_token").getAsString();
	}

	private String readResponse(HttpURLConnection kakaoServerConnection) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(kakaoServerConnection.getInputStream()));
		String responseBodyInput = "";
		StringBuilder responseBody = new StringBuilder();

		while ((responseBodyInput = br.readLine()) != null) {
			responseBody.append(responseBodyInput);
		}

		br.close();
		return responseBody.toString();
	}

}
