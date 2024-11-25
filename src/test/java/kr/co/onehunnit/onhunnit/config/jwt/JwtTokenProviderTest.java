package kr.co.onehunnit.onhunnit.config.jwt;

import static kr.co.onehunnit.onhunnit.domain.account.Provider.*;
import static org.assertj.core.api.Assertions.*;

import java.security.Key;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import kr.co.onehunnit.onhunnit.dto.account.TokenAccountInfoDto;
import kr.co.onehunnit.onhunnit.dto.token.TokenInfoDto;

@ActiveProfiles("test")
@SpringBootTest
class JwtTokenProviderTest {

	private static Key key;
	private static final int ACCESSTOKEN_EXPIRATION_TIME = 1;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@BeforeAll
	static void makeKey(@Value("${jwt.secret}") String secretKey) {
		key = Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	@DisplayName("Email과 Provider를 통해 JWT 토큰을 생성한다.")
	@Test
	void generateToken() throws ParseException {
		// given
		Authentication authentication = createAuthentication();

		// when
		TokenInfoDto tokenInfoDto = jwtTokenProvider.generateToken(authentication);

		// then
		assertThat(tokenInfoDto.getGrantType()).isEqualTo("Bearer");
		assertThat(tokenInfoDto.getAccessToken()).isNotNull();
		assertThat(tokenInfoDto.getRefreshToken()).isNotNull();
	}

	@DisplayName("JWT가 유효하지 않으면 예외가 발생한다.")
	@Test
	void validateToken() throws ParseException {
		// given
		Authentication authentication = createAuthentication();
		String accessToken = jwtTokenProvider.generateToken(authentication).getAccessToken();
		String malformedToken = accessToken + "malformed";
		String expiredToken = createToken(authentication, ACCESSTOKEN_EXPIRATION_TIME);

		String[] tokenParts = accessToken.split("\\.");
		String header = tokenParts[0];
		String payload = tokenParts[1];
		String tamperedSignature = ""; // 서명을 비워서 변조
		String tamperedToken = header + "." + payload + "." + tamperedSignature;

		// when // then
		assertThat(jwtTokenProvider.validateToken(accessToken)).isEqualTo(true);
		assertThatThrownBy(() -> jwtTokenProvider.validateToken(malformedToken))
			.isInstanceOf(MalformedJwtException.class)
			.hasMessage("유효하지 않은 토큰입니다.");
		assertThatThrownBy(() -> jwtTokenProvider.validateToken(expiredToken))
			.isInstanceOf(JwtException.class)
			.hasMessage("만료된 토큰입니다.");
		assertThatThrownBy(() -> jwtTokenProvider.validateToken(tamperedToken))
			.isInstanceOf(UnsupportedJwtException.class)
			.hasMessage("변조된 토큰입니다.");
		assertThatThrownBy(() -> jwtTokenProvider.validateToken(""))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("토큰이 존재하지 않습니다.");
	}

	@DisplayName("JWT에서 Email과 Provider 정보를 추출할 수 있다.")
	@Test
	void extractTokenInfoFromJwt() {
		// given
		Authentication authentication = createAuthentication();
		String accessToken = "Bearer " + jwtTokenProvider.generateToken(authentication).getAccessToken();

		// when
		TokenAccountInfoDto.TokenInfo tokenInfo = jwtTokenProvider.extractTokenInfoFromJwt(accessToken);

		// then
		assertThat(tokenInfo.getEmail()).isEqualTo("test@daum.net");
		assertThat(tokenInfo.getProvider()).isEqualTo("KAKAO");
	}

	private String createToken(Authentication authentication, int expirationTime) {
		Date tokenExpiration = new Date(new Date().getTime() + expirationTime);
		return Jwts.builder()
			.setSubject(authentication.getName())
			.claim("auth", getAuthorities(authentication))
			.setExpiration(tokenExpiration)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	private String getAuthorities(Authentication authentication) {
		return authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));
	}

	private Authentication createAuthentication() {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

		Authentication authentication
			= new UsernamePasswordAuthenticationToken("test@daum.net" + "," + KAKAO, null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return authentication;
	}

}