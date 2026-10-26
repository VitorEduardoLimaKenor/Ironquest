package br.edu.utfpr.ironquest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * Garante que as regras críticas de integridade estão no banco (migrações Flyway), e não só no código Java.
 * Cada teste roda em transação revertida ao final. Violações de CHECK chegam como erro MySQL 3819, que o
 * Spring JDBC não classifica; por isso esses testes conferem o nome da restrição na mensagem.
 */
@Transactional
class RestricoesBancoTest extends TesteIntegracao {

	@Autowired
	private JdbcTemplate jdbc;

	private long usuarioId;
	private long personagemId;

	@BeforeEach
	void criarPersonagem() {
		jdbc.update("INSERT INTO usuario (nome, email, senha_hash) VALUES ('Teste', 'restricoes@ironquest.dev', 'hash')");
		usuarioId = ultimoId();
		jdbc.update("""
				INSERT INTO personagem (usuario_id, nome, objetivo_principal, frequencia_semanal_alvo)
				VALUES (?, 'Heroi', 'FORCA', 3)""", usuarioId);
		personagemId = ultimoId();
	}

	@Test
	void seedCarregadoPeloFlyway() {
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM papel", Integer.class)).isEqualTo(3);
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM grupo_muscular", Integer.class)).isGreaterThanOrEqualTo(10);
		assertThat(jdbc.queryForObject("SELECT nome FROM exercicio WHERE id = 1", String.class))
				.isEqualTo("Supino reto com barra");
	}

	@Test
	void chaveDeIdempotenciaImpedePremiacaoDuplicada() {
		String sql = """
				INSERT INTO evento_progressao (personagem_id, tipo, regra, descricao, experiencia, chave_idempotencia, ocorrido_em)
				VALUES (?, 'EXPERIENCIA_GANHA', 'XP_BASE_SESSAO', 'Sessão finalizada', 100, 'SESSAO:1:XP_BASE', NOW(6))""";
		jdbc.update(sql, personagemId);

		assertThatThrownBy(() -> jdbc.update(sql, personagemId)).isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void conquistaEhConcedidaUmaUnicaVez() {
		String sql = "INSERT INTO conquista_personagem (personagem_id, conquista_id, obtida_em) VALUES (?, 1, NOW(6))";
		jdbc.update(sql, personagemId);

		assertThatThrownBy(() -> jdbc.update(sql, personagemId)).isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void usuarioTemNoMaximoUmPersonagem() {
		assertThatThrownBy(() -> jdbc.update("""
				INSERT INTO personagem (usuario_id, nome, objetivo_principal, frequencia_semanal_alvo)
				VALUES (?, 'Clone', 'FORCA', 3)""", usuarioId))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void checkRejeitaRpeForaDaEscala() {
		long sessaoId = inserirSessao();

		assertThatThrownBy(() -> jdbc.update("""
				INSERT INTO serie (sessao_treino_id, exercicio_id, ordem, tipo, repeticoes, carga_kg, rpe, volume_kg, um_rm_estimado_kg)
				VALUES (?, 1, 1, 'TRABALHO', 5, 100, 11, 500, 116.67)""", sessaoId))
				.isInstanceOf(DataAccessException.class)
				.hasMessageContaining("ck_serie_rpe");
	}

	@Test
	void missaoContraChefeExigeChefeValido() {
		assertThatThrownBy(() -> jdbc.update("""
				INSERT INTO missao (personagem_id, titulo, tipo, status, inicia_em, prazo_em)
				VALUES (?, 'Golem de Ferro', 'CHEFE', 'ATIVA', NOW(6), NOW(6) + INTERVAL 7 DAY)""", personagemId))
				.isInstanceOf(DataAccessException.class)
				.hasMessageContaining("ck_missao_chefe");

		jdbc.update("""
				INSERT INTO missao (personagem_id, titulo, tipo, status, inicia_em, prazo_em, nome_chefe, vida_total_chefe, vida_restante_chefe)
				VALUES (?, 'Golem de Ferro', 'CHEFE', 'ATIVA', NOW(6), NOW(6) + INTERVAL 7 DAY, 'Golem', 5000, 5000)""",
				personagemId);
	}

	@Test
	void exclusaoDoUsuarioRemoveTodoOHistoricoEmCascata() {
		long sessaoId = inserirSessao();
		jdbc.update("""
				INSERT INTO serie (sessao_treino_id, exercicio_id, ordem, tipo, repeticoes, carga_kg, volume_kg, um_rm_estimado_kg)
				VALUES (?, 1, 1, 'TRABALHO', 5, 100, 500, 116.67)""", sessaoId);
		long serieId = ultimoId();
		jdbc.update("""
				INSERT INTO recorde_pessoal (usuario_id, exercicio_id, tipo, valor, serie_id, alcancado_em)
				VALUES (?, 1, 'MAIOR_CARGA', 100, ?, NOW(6))""", usuarioId, serieId);
		jdbc.update("""
				INSERT INTO evento_progressao (personagem_id, tipo, regra, descricao, experiencia, chave_idempotencia, sessao_treino_id, ocorrido_em)
				VALUES (?, 'EXPERIENCIA_GANHA', 'XP_BASE_SESSAO', 'Sessão finalizada', 100, 'SESSAO:CASCATA:XP', ?, NOW(6))""",
				personagemId, sessaoId);

		jdbc.update("DELETE FROM usuario WHERE id = ?", usuarioId);

		for (String tabela : new String[] { "personagem", "sessao_treino", "recorde_pessoal" }) {
			assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM " + tabela + " WHERE usuario_id = ?", Integer.class,
					usuarioId)).as(tabela).isZero();
		}
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM evento_progressao WHERE personagem_id = ?",
				Integer.class, personagemId)).isZero();
	}

	@Test
	void dataTreinoEhObrigatoria() {
		assertThatThrownBy(() -> jdbc.update(
				"INSERT INTO sessao_treino (usuario_id, status, iniciada_em) VALUES (?, 'EM_ANDAMENTO', NOW(6))",
				usuarioId))
				.isInstanceOf(DataAccessException.class)
				.hasMessageContaining("data_treino");
	}

	private long inserirSessao() {
		jdbc.update("""
				INSERT INTO sessao_treino (usuario_id, status, iniciada_em, data_treino)
				VALUES (?, 'EM_ANDAMENTO', NOW(6), CURDATE())""", usuarioId);
		return ultimoId();
	}

	private long ultimoId() {
		return jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
	}
}
