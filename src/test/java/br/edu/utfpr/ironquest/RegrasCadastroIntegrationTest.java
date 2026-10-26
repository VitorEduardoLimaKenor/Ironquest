package br.edu.utfpr.ironquest;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;

/**
 * Aulas 07, 08, 09 e 14: evento que preenche o Location, atualização e exclusão, atualização parcial e a regra
 * que impede usuário inativo de registrar treinos.
 */
class RegrasCadastroIntegrationTest extends TesteIntegracao {

	@Test
	void eventoDeRecursoCriadoPreencheLocation() throws Exception {
		var usuario = postar("/api/usuarios", """
				{"nome": "Bia", "email": "%s@ironquest.dev", "senha": "senhaForte123"}
				""".formatted(UUID.randomUUID())).andExpect(status().isCreated());
		long usuarioId = idDe(usuario);
		usuario.andExpect(header().string("Location", endsWith("/api/usuarios/" + usuarioId)));

		var personagem = postar("/api/usuarios/" + usuarioId + "/personagem", """
				{"nome": "Bia, a Forte", "objetivoPrincipal": "HIPERTROFIA", "frequenciaSemanalAlvo": 4}
				""").andExpect(status().isCreated());
		personagem.andExpect(header().string("Location", endsWith("/api/personagens/" + idDe(personagem))));

		long sessaoId = iniciarSessao(usuarioId, "{}");
		var serie = adicionarSerie(sessaoId, SUPINO_RETO, "TRABALHO", 8, "60").andExpect(status().isCreated());
		serie.andExpect(header().string("Location",
				endsWith("/api/sessoes-treino/" + sessaoId + "/series/" + idDe(serie))));
	}

	@Test
	void atualizacaoParcialAlteraSomenteOAtivo() throws Exception {
		long usuarioId = criarUsuario();

		enviar(put("/api/usuarios/" + usuarioId + "/ativo"), "false").andExpect(status().isNoContent());

		mvc.perform(get("/api/usuarios/" + usuarioId))
				.andExpect(jsonPath("$.ativo").value(false))
				.andExpect(jsonPath("$.nome").value("Jogador"));
		mvc.perform(get("/api/usuarios").param("ativo", "false"))
				.andExpect(jsonPath("$[*].id", hasItem((int) usuarioId)));
	}

	@Test
	void usuarioInativoNaoRegistraTreino() throws Exception {
		long usuarioId = criarUsuario();
		long sessaoId = iniciarSessao(usuarioId, "{}");

		enviar(put("/api/usuarios/" + usuarioId + "/ativo"), "false").andExpect(status().isNoContent());

		adicionarSerie(sessaoId, SUPINO_RETO, "TRABALHO", 5, "80")
				.andExpect(status().isUnprocessableContent())
				.andExpect(jsonPath("$.detail")
						.value("O usuário " + usuarioId + " está inativo e não pode registrar treinos"));
		postar("/api/usuarios/" + usuarioId + "/planos-treino", """
				{"nome": "Treino C", "itens": [{"exercicioId": 1, "seriesAlvo": 3, "repeticoesAlvo": 10}]}
				""").andExpect(status().isUnprocessableContent());

		enviar(put("/api/usuarios/" + usuarioId + "/ativo"), "true").andExpect(status().isNoContent());
		adicionarSerie(sessaoId, SUPINO_RETO, "TRABALHO", 5, "80").andExpect(status().isCreated());
	}

	@Test
	void correcaoDeSerieRecalculaVolumeE1Rm() throws Exception {
		long sessaoId = iniciarSessao(criarUsuario(), "{}");
		long serieId = idDe(adicionarSerie(sessaoId, AGACHAMENTO, "TRABALHO", 5, "100"));

		enviar(put("/api/sessoes-treino/" + sessaoId + "/series/" + serieId), """
				{"exercicioId": 11, "tipo": "TRABALHO", "repeticoes": 3, "cargaKg": 120, "rpe": 9}
				""")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.volumeKg").value(360.00))
				.andExpect(jsonPath("$.umRmEstimadoKg").value(132.00));

		mvc.perform(delete("/api/sessoes-treino/" + sessaoId + "/series/" + serieId))
				.andExpect(status().isNoContent());
		mvc.perform(get("/api/sessoes-treino/" + sessaoId + "/series/" + serieId))
				.andExpect(status().isNotFound());
	}

	@Test
	void sessaoFinalizadaNaoPodeSerExcluida() throws Exception {
		long usuarioId = criarUsuario();
		long finalizadaId = iniciarSessao(usuarioId, "{}");
		adicionarSerie(finalizadaId, SUPINO_RETO, "TRABALHO", 5, "70");
		postar("/api/sessoes-treino/" + finalizadaId + "/finalizar", "{}").andExpect(status().isOk());

		mvc.perform(delete("/api/sessoes-treino/" + finalizadaId)).andExpect(status().isUnprocessableContent());

		long canceladaId = iniciarSessao(usuarioId, "{}");
		postar("/api/sessoes-treino/" + canceladaId + "/cancelar", "").andExpect(status().isOk());
		mvc.perform(delete("/api/sessoes-treino/" + canceladaId)).andExpect(status().isNoContent());
	}

	@Test
	void exclusaoDoUsuarioRemoveSeuHistorico() throws Exception {
		long usuarioId = criarUsuario();
		long sessaoId = iniciarSessao(usuarioId, "{}");

		mvc.perform(delete("/api/usuarios/" + usuarioId)).andExpect(status().isNoContent());

		mvc.perform(get("/api/usuarios/" + usuarioId)).andExpect(status().isNotFound());
		mvc.perform(get("/api/sessoes-treino/" + sessaoId)).andExpect(status().isNotFound());
	}
}
