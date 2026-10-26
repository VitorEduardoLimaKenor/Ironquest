package br.edu.utfpr.ironquest;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

/**
 * Fluxo ponta a ponta pela API REST contra MySQL real: cadastro, personagem, sessão, séries, finalização
 * idempotente e recordes pessoais.
 */
class FluxoTreinoIntegrationTest extends TesteIntegracao {

	@Test
	void registraSessaoCalculaVolumeERecordesSemPremiarDuasVezes() throws Exception {
		long usuarioId = criarUsuario();

		postar("/api/usuarios/" + usuarioId + "/personagem", """
				{"nome": "Heroi", "objetivoPrincipal": "FORCA", "frequenciaSemanalAlvo": 3}
				""")
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.nivel").value(1))
				.andExpect(jsonPath("$.usuario.id").value(usuarioId))
				.andExpect(jsonPath("$.atributos", hasSize(4)));

		long sessaoId = iniciarSessao(usuarioId, "{}");

		adicionarSerie(sessaoId, SUPINO_RETO, "AQUECIMENTO", 10, "60").andExpect(status().isCreated());
		adicionarSerie(sessaoId, SUPINO_RETO, "TRABALHO", 5, "100")
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.exercicio.nome").value("Supino reto com barra"))
				.andExpect(jsonPath("$.volumeKg").value(500.00))
				.andExpect(jsonPath("$.umRmEstimadoKg").value(116.67));
		adicionarSerie(sessaoId, AGACHAMENTO, "TRABALHO", 5, "120").andExpect(status().isCreated());

		postar("/api/sessoes-treino/" + sessaoId + "/finalizar", "{}")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.jaEstavaFinalizada").value(false))
				// Aquecimento fica fora: 100×5 + 120×5.
				.andExpect(jsonPath("$.sessao.volumeTotalKg").value(1100.00))
				.andExpect(jsonPath("$.novosRecordes", hasSize(6)));

		// Reenvio da mesma finalização: nenhum recálculo nem recorde duplicado.
		postar("/api/sessoes-treino/" + sessaoId + "/finalizar", "{}")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.jaEstavaFinalizada").value(true))
				.andExpect(jsonPath("$.novosRecordes", empty()));

		adicionarSerie(sessaoId, SUPINO_RETO, "TRABALHO", 5, "100").andExpect(status().isUnprocessableContent());

		mvc.perform(get("/api/usuarios/" + usuarioId + "/recordes").param("exercicioId", String.valueOf(SUPINO_RETO)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].tipo",
						containsInAnyOrder("MAIOR_CARGA", "MAIOR_1RM_ESTIMADO", "MAIOR_VOLUME_SERIE")))
				.andExpect(jsonPath("$[?(@.tipo == 'MAIOR_CARGA')].valor", contains(100.0)))
				.andExpect(jsonPath("$[?(@.tipo == 'MAIOR_1RM_ESTIMADO')].valor", contains(116.67)));
	}
}
