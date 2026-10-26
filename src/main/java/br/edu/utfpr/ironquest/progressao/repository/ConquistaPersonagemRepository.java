package br.edu.utfpr.ironquest.progressao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.utfpr.ironquest.progressao.domain.ConquistaPersonagem;

public interface ConquistaPersonagemRepository extends JpaRepository<ConquistaPersonagem, Long> {

	@EntityGraph(attributePaths = "conquista")
	List<ConquistaPersonagem> findByPersonagemIdOrderByObtidaEmDesc(Long personagemId);

	boolean existsByConquistaId(Long conquistaId);
}
