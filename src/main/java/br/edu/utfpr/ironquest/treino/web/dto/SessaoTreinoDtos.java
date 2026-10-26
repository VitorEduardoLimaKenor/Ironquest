package br.edu.utfpr.ironquest.treino.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import br.edu.utfpr.ironquest.shared.web.dto.ReferenciaResponse;
import br.edu.utfpr.ironquest.treino.domain.PlanoTreino;
import br.edu.utfpr.ironquest.treino.domain.RecordePessoal;
import br.edu.utfpr.ironquest.treino.domain.Serie;
import br.edu.utfpr.ironquest.treino.domain.SessaoTreino;
import br.edu.utfpr.ironquest.treino.domain.StatusSessao;
import br.edu.utfpr.ironquest.treino.domain.TipoRecorde;
import br.edu.utfpr.ironquest.treino.domain.TipoSerie;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

/** DTOs de sessões de treino, séries e recordes pessoais. */
public final class SessaoTreinoDtos {

	private SessaoTreinoDtos() {
	}

	/**
	 * Sem {@code iniciadaEm}, a sessão começa no instante da requisição. A data do treino é calculada pela API
	 * no fuso configurado.
	 */
	public record IniciarSessaoRequest(
			Long planoTreinoId,
			@PastOrPresent Instant iniciadaEm,
			@Size(max = 500) String observacoes) {
	}

	/** Usado para registrar e para corrigir uma série. */
	public record SerieRequest(
			@NotNull Long exercicioId,
			@NotNull TipoSerie tipo,
			@NotNull @Min(1) @Max(100) Integer repeticoes,
			@NotNull @DecimalMin("0.00") @DecimalMax("1000.00") @Digits(integer = 4, fraction = 2) BigDecimal cargaKg,
			@DecimalMin("1.0") @DecimalMax("10.0") @Digits(integer = 2, fraction = 1) BigDecimal rpe,
			@Min(0) @Max(10) Integer rir) {
	}

	/** Sem {@code finalizadaEm}, a sessão termina no instante da requisição. */
	public record FinalizarSessaoRequest(@PastOrPresent Instant finalizadaEm) {
	}

	/** Item da pesquisa paginada: dados da sessão com os recursos relacionados. */
	public record SessaoTreinoResumoResponse(
			Long id,
			ReferenciaResponse usuario,
			ReferenciaResponse planoTreino,
			StatusSessao status,
			LocalDate dataTreino,
			Instant iniciadaEm,
			Instant finalizadaEm,
			BigDecimal volumeTotalKg) {

		public static SessaoTreinoResumoResponse de(SessaoTreino sessao) {
			return new SessaoTreinoResumoResponse(sessao.getId(), referenciaUsuario(sessao),
					referenciaPlano(sessao.getPlanoTreino()), sessao.getStatus(), sessao.getDataTreino(), sessao.getIniciadaEm(), sessao.getFinalizadaEm(),
					sessao.getVolumeTotalKg());
		}
	}

	public record SessaoTreinoResponse(
			Long id,
			ReferenciaResponse usuario,
			ReferenciaResponse planoTreino,
			StatusSessao status,
			LocalDate dataTreino,
			Instant iniciadaEm,
			Instant finalizadaEm,
			BigDecimal volumeTotalKg,
			String observacoes,
			List<SerieResponse> series) {

		public static SessaoTreinoResponse de(SessaoTreino sessao) {
			return new SessaoTreinoResponse(sessao.getId(), referenciaUsuario(sessao),
					referenciaPlano(sessao.getPlanoTreino()), sessao.getStatus(), sessao.getDataTreino(), sessao.getIniciadaEm(), sessao.getFinalizadaEm(),
					sessao.getVolumeTotalKg(), sessao.getObservacoes(),
					sessao.getSeries().stream().map(SerieResponse::de).toList());
		}
	}

	public record SerieResponse(
			Long id,
			int ordem,
			ReferenciaResponse exercicio,
			TipoSerie tipo,
			int repeticoes,
			BigDecimal cargaKg,
			BigDecimal rpe,
			Integer rir,
			BigDecimal volumeKg,
			BigDecimal umRmEstimadoKg) {

		public static SerieResponse de(Serie serie) {
			return new SerieResponse(serie.getId(), serie.getOrdem(),
					new ReferenciaResponse(serie.getExercicio().getId(), serie.getExercicio().getNome()),
					serie.getTipo(), serie.getRepeticoes(), serie.getCargaKg(), serie.getRpe(), serie.getRir(),
					serie.getVolumeKg(), serie.getUmRmEstimadoKg());
		}
	}

	/**
	 * Resultado da finalização. Em um reenvio, {@code jaEstavaFinalizada} é verdadeiro e nenhum recorde é
	 * recalculado.
	 */
	public record FinalizacaoSessaoResponse(
			SessaoTreinoResponse sessao,
			List<RecordePessoalResponse> novosRecordes,
			boolean jaEstavaFinalizada) {
	}

	public record RecordePessoalResponse(
			Long id,
			ReferenciaResponse exercicio,
			TipoRecorde tipo,
			BigDecimal valor,
			Long serieId,
			Instant alcancadoEm) {

		public static RecordePessoalResponse de(RecordePessoal recorde) {
			return new RecordePessoalResponse(recorde.getId(),
					new ReferenciaResponse(recorde.getExercicio().getId(), recorde.getExercicio().getNome()),
					recorde.getTipo(), recorde.getValor(),
					recorde.getSerie() == null ? null : recorde.getSerie().getId(), recorde.getAlcancadoEm());
		}
	}

	private static ReferenciaResponse referenciaUsuario(SessaoTreino sessao) {
		return new ReferenciaResponse(sessao.getUsuario().getId(), sessao.getUsuario().getNome());
	}

	private static ReferenciaResponse referenciaPlano(PlanoTreino plano) {
		return plano == null ? null : new ReferenciaResponse(plano.getId(), plano.getNome());
	}
}
