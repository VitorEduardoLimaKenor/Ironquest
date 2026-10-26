package br.edu.utfpr.ironquest.progressao.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import br.edu.utfpr.ironquest.personagem.domain.TipoAtributo;
import br.edu.utfpr.ironquest.progressao.domain.Conquista;
import br.edu.utfpr.ironquest.progressao.domain.ConquistaPersonagem;
import br.edu.utfpr.ironquest.progressao.domain.EventoProgressao;
import br.edu.utfpr.ironquest.progressao.domain.MetricaObjetivo;
import br.edu.utfpr.ironquest.progressao.domain.Missao;
import br.edu.utfpr.ironquest.progressao.domain.Objetivo;
import br.edu.utfpr.ironquest.progressao.domain.Recompensa;
import br.edu.utfpr.ironquest.progressao.domain.StatusMissao;
import br.edu.utfpr.ironquest.progressao.domain.TipoEvento;
import br.edu.utfpr.ironquest.progressao.domain.TipoMissao;
import br.edu.utfpr.ironquest.shared.web.dto.ReferenciaResponse;
import br.edu.utfpr.ironquest.treino.domain.Exercicio;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** DTOs do módulo de progressão. */
public final class ProgressaoDtos {

	private ProgressaoDtos() {
	}

	public record RecompensaRequest(
			@NotBlank @Size(max = 100) String nome,
			@Size(max = 255) String descricao,
			@NotNull @Min(0) @Max(10000) Integer experiencia,
			TipoAtributo tipoAtributo,
			@Min(0) @Max(10) Integer pontosAtributo) {
	}

	public record RecompensaResponse(
			Long id,
			String nome,
			String descricao,
			int experiencia,
			TipoAtributo tipoAtributo,
			int pontosAtributo) {

		public static RecompensaResponse de(Recompensa recompensa) {
			return new RecompensaResponse(recompensa.getId(), recompensa.getNome(), recompensa.getDescricao(),
					recompensa.getExperiencia(), recompensa.getTipoAtributo(), recompensa.getPontosAtributo());
		}
	}

	public record ConquistaRequest(
			@NotBlank @Size(max = 50) @Pattern(regexp = "[A-Z0-9_]+") String codigo,
			@NotBlank @Size(max = 100) String nome,
			@NotBlank @Size(max = 255) String descricao,
			@NotBlank @Size(max = 255) String regra,
			Long recompensaId) {
	}

	public record ConquistaResponse(
			Long id,
			String codigo,
			String nome,
			String descricao,
			String regra,
			RecompensaResponse recompensa) {

		public static ConquistaResponse de(Conquista conquista) {
			return new ConquistaResponse(conquista.getId(), conquista.getCodigo(), conquista.getNome(),
					conquista.getDescricao(), conquista.getRegra(),
					conquista.getRecompensa() == null ? null : RecompensaResponse.de(conquista.getRecompensa()));
		}
	}

	public record ConquistaObtidaResponse(Long conquistaId, String codigo, String nome, Instant obtidaEm) {

		public static ConquistaObtidaResponse de(ConquistaPersonagem obtida) {
			Conquista conquista = obtida.getConquista();
			return new ConquistaObtidaResponse(conquista.getId(), conquista.getCodigo(), conquista.getNome(),
					obtida.getObtidaEm());
		}
	}

	public record MissaoResponse(
			Long id,
			String titulo,
			String descricao,
			TipoMissao tipo,
			StatusMissao status,
			Instant iniciaEm,
			Instant prazoEm,
			Instant concluidaEm,
			ReferenciaResponse recompensa,
			String nomeChefe,
			Integer vidaTotalChefe,
			Integer vidaRestanteChefe,
			List<ObjetivoResponse> objetivos) {

		public static MissaoResponse de(Missao missao) {
			Recompensa recompensa = missao.getRecompensa();
			return new MissaoResponse(missao.getId(), missao.getTitulo(), missao.getDescricao(), missao.getTipo(),
					missao.getStatus(), missao.getIniciaEm(), missao.getPrazoEm(), missao.getConcluidaEm(),
					recompensa == null ? null : new ReferenciaResponse(recompensa.getId(), recompensa.getNome()),
					missao.getNomeChefe(), missao.getVidaTotalChefe(), missao.getVidaRestanteChefe(),
					missao.getObjetivos().stream().map(ObjetivoResponse::de).toList());
		}
	}

	public record ObjetivoResponse(
			Long id,
			String descricao,
			MetricaObjetivo metrica,
			ReferenciaResponse exercicio,
			BigDecimal valorAlvo,
			BigDecimal valorAtual,
			boolean concluido) {

		static ObjetivoResponse de(Objetivo objetivo) {
			Exercicio exercicio = objetivo.getExercicio();
			return new ObjetivoResponse(objetivo.getId(), objetivo.getDescricao(), objetivo.getMetrica(),
					exercicio == null ? null : new ReferenciaResponse(exercicio.getId(), exercicio.getNome()),
					objetivo.getValorAlvo(), objetivo.getValorAtual(), objetivo.isConcluido());
		}
	}

	public record EventoProgressaoResponse(
			Long id,
			TipoEvento tipo,
			String regra,
			String descricao,
			int experiencia,
			Long sessaoTreinoId,
			Long missaoId,
			Long conquistaId,
			Instant ocorridoEm) {

		public static EventoProgressaoResponse de(EventoProgressao evento) {
			return new EventoProgressaoResponse(evento.getId(), evento.getTipo(), evento.getRegra(),
					evento.getDescricao(), evento.getExperiencia(),
					evento.getSessaoTreino() == null ? null : evento.getSessaoTreino().getId(),
					evento.getMissao() == null ? null : evento.getMissao().getId(),
					evento.getConquista() == null ? null : evento.getConquista().getId(),
					evento.getOcorridoEm());
		}
	}
}
