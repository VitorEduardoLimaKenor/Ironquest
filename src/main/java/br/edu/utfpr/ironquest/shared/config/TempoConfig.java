package br.edu.utfpr.ironquest.shared.config;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TempoConfig {

	/** Relógio no fuso dos jogadores; define, por exemplo, a data do treino de uma sessão. */
	@Bean
	public Clock relogio(@Value("${ironquest.fuso-horario}") ZoneId fusoHorario) {
		return Clock.system(fusoHorario);
	}
}
