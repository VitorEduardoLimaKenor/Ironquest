package br.edu.utfpr.ironquest.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI ironQuestOpenApi() {
		return new OpenAPI().info(new Info()
				.title("IronQuest API")
				.version("0.2.0")
				.description("RPG de progressão física baseado em treinos reais. "
						+ "Checkpoint 2: funcionalidades (validação, eventos, filtros e paginação)."));
	}
}
