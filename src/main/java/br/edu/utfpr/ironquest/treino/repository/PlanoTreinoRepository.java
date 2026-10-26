package br.edu.utfpr.ironquest.treino.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.utfpr.ironquest.treino.domain.PlanoTreino;

public interface PlanoTreinoRepository extends JpaRepository<PlanoTreino, Long> {

	@EntityGraph(attributePaths = { "usuario", "autor", "itens", "itens.exercicio" })
	List<PlanoTreino> findByUsuarioIdOrderByNomeAsc(Long usuarioId);

	@Override
	@EntityGraph(attributePaths = { "usuario", "autor", "itens", "itens.exercicio" })
	Optional<PlanoTreino> findById(Long id);
}
