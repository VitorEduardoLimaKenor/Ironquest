package br.edu.utfpr.ironquest.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Libera o frontend Angular (origem separada) para consumir a API. */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

	private final String[] origensPermitidas;

	public CorsConfig(@Value("${ironquest.cors.origens-permitidas}") String[] origensPermitidas) {
		this.origensPermitidas = origensPermitidas;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
				.allowedOrigins(origensPermitidas)
				.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
	}
}
