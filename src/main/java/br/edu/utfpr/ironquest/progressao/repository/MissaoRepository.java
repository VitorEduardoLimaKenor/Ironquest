package br.edu.utfpr.ironquest.progressao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.utfpr.ironquest.progressao.domain.Missao;
import br.edu.utfpr.ironquest.progressao.domain.StatusMissao;

public interface MissaoRepository extends JpaRepository<Missao, Long> {

	@EntityGraph(attributePaths = { "recompensa", "objetivos", "objetivos.exercicio" })
	List<Missao> findByPersonagemIdOrderByPrazoEmAsc(Long personagemId);

	@EntityGraph(attributePaths = { "recompensa", "objetivos", "objetivos.exercicio" })
	List<Missao> findByPersonagemIdAndStatusOrderByPrazoEmAsc(Long personagemId, StatusMissao status);
}
