package kr.co.onehunnit.onhunnit.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.onehunnit.onhunnit.dto.token.IdTokenDto;
import kr.co.onehunnit.onhunnit.service.OAuthService;

@ActiveProfiles("test")
@WebMvcTest(controllers = OAuthController.class)
class OAuthControllerTest {

	private String idToken = "";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private OAuthService oAuthService;

	@DisplayName("소셜 로그인을 한다.")
	@WithMockUser
	@Test
	void kakaoLogin() throws Exception {
		// given
		IdTokenDto idTokenDto = IdTokenDto.builder()
			.idToken(idToken)
			.build();

		// when // then
		mockMvc.perform(post("/oauth/kakao")
				.content(objectMapper.writeValueAsString(idTokenDto))
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk());
	}

	@DisplayName("소셜 로그인을 할 때 id_token은 필수값이다.")
	@WithMockUser
	@Test
	void kakaoLoginWithoutIdToken() throws Exception {
		// given
		IdTokenDto idTokenDto = IdTokenDto.builder()
			.build();

		// when // then
		mockMvc.perform(post("/oauth/kakao")
				.content(objectMapper.writeValueAsString(idTokenDto))
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf())
			)
			// .andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("400"))
			.andExpect(jsonPath("$.status").value("400"))
			.andExpect(jsonPath("$.message").value("id_token은 필수입니다."));
	}

}