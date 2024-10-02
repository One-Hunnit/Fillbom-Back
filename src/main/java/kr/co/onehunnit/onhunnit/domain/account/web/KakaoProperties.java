package kr.co.onehunnit.onhunnit.domain.account.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kakao")
public class KakaoProperties {
	private String clientId;
	private String clientSecret;
	private String redirectUri;
}
