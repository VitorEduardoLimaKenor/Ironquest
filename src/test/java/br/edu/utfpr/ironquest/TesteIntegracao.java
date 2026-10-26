package br.edu.utfpr.ironquest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.jayway.jsonpath.JsonPath;

/**
 * Base dos testes de integração: contexto Spring completo, MockMvc e MySQL em container. Todas as classes
 * filhas compartilham o mesmo contexto e, portanto, o mesmo banco; cada teste cria seus próprios dados.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(MySqlTestcontainersConfig.class)
public abstract class TesteIntegracao {

	protected static final long SUPINO_RETO = 1;
	protected static final long AGACHAMENTO = 11;

	@Autowired
	protected MockMvc mvc;

	protected ResultActions enviar(MockHttpServletRequestBuilder requisicao, String json) throws Exception {
		return mvc.perform(requisicao.contentType(MediaType.APPLICATION_JSON).content(json));
	}

	protected ResultActions postar(String uri, String json) throws Exception {
		return enviar(post(uri), json);
	}

	protected long criarUsuario() throws Exception {
		return idDe(postar("/api/usuarios", """
				{"nome": "Jogador", "email": "%s@ironquest.dev", "senha": "senhaForte123"}
				""".formatted(UUID.randomUUID())));
	}

	protected long iniciarSessao(long usuarioId, String corpo) throws Exception {
		return idDe(postar("/api/usuarios/" + usuarioId + "/sessoes-treino", corpo));
	}

	protected ResultActions adicionarSerie(long sessaoId, long exercicioId, String tipo, int repeticoes,
			String carga) throws Exception {
		return postar("/api/sessoes-treino/" + sessaoId + "/series", """
				{"exercicioId": %d, "tipo": "%s", "repeticoes": %d, "cargaKg": %s}
				""".formatted(exercicioId, tipo, repeticoes, carga));
	}

	protected static long idDe(ResultActions resultado) throws Exception {
		Number id = JsonPath.read(resultado.andReturn().getResponse().getContentAsString(), "$.id");
		return id.longValue();
	}
}
