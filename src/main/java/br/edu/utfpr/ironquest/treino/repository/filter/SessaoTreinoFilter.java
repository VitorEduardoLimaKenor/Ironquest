package br.edu.utfpr.ironquest.treino.repository.filter;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import br.edu.utfpr.ironquest.treino.domain.StatusSessao;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;

/**
 * Critérios opcionais da pesquisa de sessões, recebidos como parâmetros de consulta. Campo nulo = sem filtro.
 *
 * @param dataTreinoDe data inicial (inclusiva), formato yyyy-MM-dd
 * @param dataTreinoAte data final (inclusiva), formato yyyy-MM-dd
 * @param exercicioId sessões com ao menos uma série desse exercício
 * @param observacoes trecho contido nas observações (sem diferenciar maiúsculas)
 */
public record SessaoTreinoFilter(
		Long usuarioId,
		StatusSessao status,
		Long planoTreinoId,
		Long exercicioId,
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataTreinoDe,
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataTreinoAte,
		@Size(max = 100) String observacoes) {

	@Hidden
	@AssertTrue
	public boolean isPeriodoValido() {
		return dataTreinoDe == null || dataTreinoAte == null || !dataTreinoDe.isAfter(dataTreinoAte);
	}

	/** Mesmos critérios restritos a um usuário (histórico em /api/usuarios/{id}/sessoes-treino). */
	public SessaoTreinoFilter doUsuario(Long idUsuario) {
		return new SessaoTreinoFilter(idUsuario, status, planoTreinoId, exercicioId, dataTreinoDe, dataTreinoAte,
				observacoes);
	}
}
