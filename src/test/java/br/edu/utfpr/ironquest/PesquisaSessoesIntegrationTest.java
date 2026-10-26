package br.edu.utfpr.ironquest;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Aulas 10, 11, 15 e 16: listagem com relacionamentos e datas LocalDate, filtros com Criteria API e paginação.
 * Cenário: três sessões do mesmo usuário em dias diferentes.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PesquisaSessoesIntegrationTest extends TesteIntegracao {

	private long usuarioId;
	private long planoId;
	private long sessaoPeito;
	private long sessaoPernas;
	private long sessaoCancelada;

	@BeforeAll
	void criarCenario() throws Exception {
		usuarioId = criarUsuario();
		planoId = idDe(postar("/api/usuarios/" + usuarioId + "/planos-treino", """
				{"nome": "Treino A - Peito", "itens": [{"exercicioId": 1, "seriesAlvo": 4, "repeticoesAlvo": 5}]}
				"""));

		sessaoPeito = iniciarSessao(usuarioId, """
				{"planoTreinoId": %d, "iniciadaEm": "2026-09-01T12:00:00Z", "observacoes": "Peito pesado"}
				""".formatted(planoId));
		adicionarSerie(sessaoPeito, SUPINO_RETO, "TRABALHO", 5, "100");
		finalizar(sessaoPeito, "2026-09-01T13:00:00Z");

		sessaoPernas = iniciarSessao(usuarioId, """
				{"iniciadaEm": "2026-09-15T12:00:00Z", "observacoes": "Dia de pernas"}
				""");
		adicionarSerie(sessaoPernas, AGACHAMENTO, "TRABALHO", 5, "120");
		finalizar(sessaoPernas, "2026-09-15T13:00:00Z");

		// 01:30 UTC ainda é 30/09 em São Paulo (UTC-3).
		sessaoCancelada = iniciarSessao(usuarioId, """
				{"iniciadaEm": "2026-10-01T01:30:00Z", "observacoes": "Treino interrompido"}
				""");
		postar("/api/sessoes-treino/" + sessaoCancelada + "/cancelar", "").andExpect(status().isOk());
	}

	@Test
	void listaComRelacionamentosEDataDoTreinoNoFusoDoJogador() throws Exception {
		mvc.perform(pesquisa())
				.andExpect(status().isOk())
				// Padrão: treinos mais recentes primeiro.
				.andExpect(jsonPath("$.content[*].id", contains((int) sessaoCancelada, (int) sessaoPernas,
						(int) sessaoPeito)))
				.andExpect(jsonPath("$.content[0].dataTreino").value("2026-09-30"))
				.andExpect(jsonPath("$.content[0].usuario.id").value(usuarioId))
				.andExpect(jsonPath("$.content[2].planoTreino.nome").value("Treino A - Peito"))
				.andExpect(jsonPath("$.content[1].planoTreino").doesNotExist());
	}

	@Test
	void filtraPorPeriodoStatusPlanoExercicioETexto() throws Exception {
		mvc.perform(pesquisa().param("dataTreinoDe", "2026-09-10").param("dataTreinoAte", "2026-09-30"))
				.andExpect(jsonPath("$.content[*].id", contains((int) sessaoCancelada, (int) sessaoPernas)));
		mvc.perform(pesquisa().param("status", "FINALIZADA"))
				.andExpect(jsonPath("$.page.totalElements").value(2));
		mvc.perform(pesquisa().param("planoTreinoId", String.valueOf(planoId)))
				.andExpect(jsonPath("$.content[*].id", contains((int) sessaoPeito)));
		mvc.perform(pesquisa().param("exercicioId", String.valueOf(AGACHAMENTO)))
				.andExpect(jsonPath("$.content[*].id", contains((int) sessaoPernas)));
		mvc.perform(pesquisa().param("observacoes", "PERNAS"))
				.andExpect(jsonPath("$.content[*].id", contains((int) sessaoPernas)));
	}

	@Test
	void paginaEOrdenaOResultado() throws Exception {
		mvc.perform(pesquisa().param("size", "2").param("page", "0"))
				.andExpect(jsonPath("$.content", hasSize(2)))
				.andExpect(jsonPath("$.page.size").value(2))
				.andExpect(jsonPath("$.page.number").value(0))
				.andExpect(jsonPath("$.page.totalElements").value(3))
				.andExpect(jsonPath("$.page.totalPages").value(2));
		mvc.perform(pesquisa().param("size", "2").param("page", "1"))
				.andExpect(jsonPath("$.content[*].id", contains((int) sessaoPeito)));
		mvc.perform(pesquisa().param("sort", "volumeTotalKg,desc"))
				.andExpect(jsonPath("$.content[0].id").value(sessaoPernas));
	}

	@Test
	void historicoDoUsuarioUsaOsMesmosFiltros() throws Exception {
		mvc.perform(get("/api/usuarios/" + usuarioId + "/sessoes-treino").param("status", "CANCELADA"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[*].id", contains((int) sessaoCancelada)));
	}

	@Test
	void rejeitaPeriodoInvertidoEOrdenacaoNaoPermitida() throws Exception {
		mvc.perform(pesquisa().param("dataTreinoDe", "2026-10-10").param("dataTreinoAte", "2026-10-01"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.erros[0].mensagemUsuario")
						.value("A data inicial deve ser anterior ou igual à data final"));
		mvc.perform(pesquisa().param("dataTreinoDe", "01/10/2026"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.erros[0].campo").value("dataTreinoDe"));
		mvc.perform(pesquisa().param("sort", "senha"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail")
						.value("Não é possível ordenar por 'senha'. Use: dataTreino, iniciadaEm, volumeTotalKg"));
	}

	private MockHttpServletRequestBuilder pesquisa() {
		return get("/api/sessoes-treino").param("usuarioId", String.valueOf(usuarioId));
	}

	private void finalizar(long sessaoId, String finalizadaEm) throws Exception {
		postar("/api/sessoes-treino/" + sessaoId + "/finalizar", """
				{"finalizadaEm": "%s"}
				""".formatted(finalizadaEm)).andExpect(status().isOk());
	}
}
