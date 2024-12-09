package kr.co.onehunnit.onhunnit.service;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.onehunnit.onhunnit.config.exception.ApiException;
import kr.co.onehunnit.onhunnit.config.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class AppleSocialService {

	private static final String APPLE_KEYS_URL = "https://appleid.apple.com/auth/keys";
	private static final String ISSUER = "https://appleid.apple.com";

	public DecodedJWT decodeAppleIdToken(String idToken) {
		String[] tokenParts = idToken.split("\\.");
		Map<String, Object> header = decodeHeader(tokenParts[0]);
		RSAPublicKey publicKey = getPublicKey(header);
		JWTVerifier verifier = createJWTVerifier(publicKey);
		return verifier.verify(idToken);
	}

	private Map<String, Object> decodeHeader(String headerPart) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(
				new String(Base64.getUrlDecoder().decode(headerPart)),
				new TypeReference<Map<String, Object>>() {
				}
			);
		} catch (Exception e) {
			throw new ApiException(ErrorCode.FAIL_TO_DECODE);
		}
	}

	private RSAPublicKey getPublicKey(Map<String, Object> header) {
		String keyId = header.get("kid").toString();
		try {
			JwkProvider provider = new UrlJwkProvider(new URL(APPLE_KEYS_URL));
			return (RSAPublicKey)provider.get(keyId).getPublicKey();
		} catch (Exception e) {
			throw new ApiException(ErrorCode.FAIL_TO_GET_PUBLIC_KEY);
		}
	}

	private JWTVerifier createJWTVerifier(RSAPublicKey publicKey) {
		Algorithm algorithm = Algorithm.RSA256(publicKey, null);
		return JWT.require(algorithm)
			.withIssuer(ISSUER)
			.build();
	}

}
