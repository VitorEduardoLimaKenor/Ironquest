package br.edu.utfpr.ironquest;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;

/**
 * Aulas 04, 05, 06, 12 e 13: atributos desconhecidos, ExceptionHandler com mensagens do messages.properties,
 * Bean Validation e inconsistências barradas pelo banco.
 */
class TratamentoErrosIntegrationTest extends TesteIntegracao {

	@Test
	void atributoDesconhecidoNoJsonRetorna400() throws Exception {
		postar("/api/usuarios", """
				{"nome": "Ana", "email": "ana@ironquest.dev", "senha": "senhaForte123", "apelido": "aninha"}
				""")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").value("Mensagem inválida"))
				.andExpect(jsonPath("$.erros[0].mensagemUsuario").value("O atributo 'apelido' não é reconhecido"))
				.andExpect(jsonPath("$.erros[0].mensagemDesenvolvedor",
						containsString("UnrecognizedPropertyException")));
	}

	@Test
	void valorForaDoEnumIndicaOAtributo() throws Exception {
		postar("/api/exercicios", """
				{"nome": "Remada", "grupoMuscularId": 2, "tipo": "QUALQUER", "equipamento": "BARRA"}
				""")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Valor inválido para o atributo 'tipo'"));
	}

	@Test
	void jsonMalformadoOuCorpoAusenteRetornam400() throws Exception {
		postar("/api/usuarios", "{\"nome\": ")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Mensagem inválida"));

		mvc.perform(put("/api/usuarios/1/ativo"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("O corpo da requisição é obrigatório"));
	}

	@Test
	void beanValidationUsaRotulosDoMessagesProperties() throws Exception {
		postar("/api/usuarios", """
				{"nome": "", "email": "invalido", "senha": "123"}
				""")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").value("Dados inválidos"))
				.andExpect(jsonPath("$.erros[?(@.campo == 'nome')].mensagemUsuario").value("Nome é obrigatório(a)"))
				.andExpect(jsonPath("$.erros[?(@.campo == 'email')].mensagemUsuario")
						.value("E-mail deve ser um e-mail válido"))
				.andExpect(jsonPath("$.erros[?(@.campo == 'senha')].mensagemUsuario")
						.value("Senha deve ter entre 8 e 72 caracteres"));
	}

	@Test
	void validacaoAlcancaItensAninhados() throws Exception {
		long usuarioId = criarUsuario();

		postar("/api/usuarios/" + usuarioId + "/planos-treino", """
				{"nome": "Treino A", "itens": [{"exercicioId": 1, "seriesAlvo": 50, "repeticoesAlvo": 8}]}
				""")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.erros[0].campo").value("itens[0].seriesAlvo"))
				.andExpect(jsonPath("$.erros[0].mensagemUsuario")
						.value("itens[0].seriesAlvo deve ser menor ou igual a 20"));
	}

	@Test
	void parametroComTipoErradoERotaInexistente() throws Exception {
		mvc.perform(get("/api/usuarios/abc"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("Valor 'abc' inválido para o parâmetro 'id'"));

		mvc.perform(get("/api/rota-que-nao-existe"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.title").value("Recurso não encontrado"));
	}

	@Test
	void recursoNaoEncontradoRetorna404() throws Exception {
		mvc.perform(get("/api/sessoes-treino/999999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.detail").value("Sessão de treino de id 999999 não encontrado(a)"));
	}

	@Test
	void exclusaoDeExercicioComHistoricoEhBarradaPeloBanco() throws Exception {
		long usuarioId = criarUsuario();
		long exercicioId = criarExercicio();
		postar("/api/usuarios/" + usuarioId + "/planos-treino", """
				{"nome": "Treino B", "itens": [{"exercicioId": %d, "seriesAlvo": 3, "repeticoesAlvo": 10}]}
				""".formatted(exercicioId)).andExpect(status().isCreated());

		mvc.perform(delete("/api/exercicios/" + exercicioId))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.title").value("Operação não permitida"))
				.andExpect(jsonPath("$.erros[0].mensagemDesenvolvedor", containsString("foreign key")));
	}

	@Test
	void exercicioSemHistoricoPodeSerExcluido() throws Exception {
		long exercicioId = criarExercicio();

		mvc.perform(delete("/api/exercicios/" + exercicioId)).andExpect(status().isNoContent());
		mvc.perform(get("/api/exercicios/" + exercicioId)).andExpect(status().isNotFound());
	}

	private long criarExercicio() throws Exception {
		return idDe(postar("/api/exercicios", """
				{"nome": "Exercício %s", "grupoMuscularId": 4, "tipo": "ISOLADO", "equipamento": "HALTER"}
				""".formatted(UUID.randomUUID())));
	}
}
