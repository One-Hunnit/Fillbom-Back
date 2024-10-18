package kr.co.onehunnit.onhunnit.config.jwt;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws JwtException, IOException, ServletException {
		String token = resolveToken((HttpServletRequest) request);

		try {
			if (token != null && jwtTokenProvider.validateToken(token)) {
				Authentication authentication = jwtTokenProvider.getAuthentication(token);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (ExpiredJwtException e) {
			setErrorResponse((HttpServletResponse) response, "만료된 토큰입니다..", HttpStatus.UNAUTHORIZED);
			return;
		} catch (UnsupportedJwtException e) {
			setErrorResponse((HttpServletResponse) response, "변조된 토큰입니다.", HttpStatus.UNAUTHORIZED);
			return;
		} catch (Exception e) {
			setErrorResponse((HttpServletResponse) response, "토큰이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED);
			return;
		}
		chain.doFilter(request, response);
	}

	private void setErrorResponse(HttpServletResponse response, String message, HttpStatus status) throws IOException {
		response.setStatus(status.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("{\"error\": \"" + message + "\"}");
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}

		return null;
	}

}
