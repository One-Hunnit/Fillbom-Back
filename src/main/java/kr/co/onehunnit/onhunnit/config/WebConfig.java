package kr.co.onehunnit.onhunnit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class WebConfig {

	@Bean
	public DefaultUriBuilderFactory defaultUriBuilderFactory() {
		DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
		uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
		return uriBuilderFactory;
	}

	@Bean
	public WebClient webClient() {
		return WebClient.builder()
			.uriBuilderFactory(defaultUriBuilderFactory())
			.build();
	}
}
