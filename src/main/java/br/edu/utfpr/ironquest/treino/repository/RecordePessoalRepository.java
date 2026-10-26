package br.edu.utfpr.ironquest.treino.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.utfpr.ironquest.treino.domain.RecordePessoal;

public interface RecordePessoalRepository extends JpaRepository<RecordePessoal, Long> {

	@EntityGraph(attributePaths = "exercicio")
	List<RecordePessoal> findByUsuarioIdOrderByExercicioNomeAscTipoAsc(Long usuarioId);

	@EntityGraph(attributePaths = "exercicio")
	List<RecordePessoal> findByUsuarioIdAndExercicioIdOrderByTipoAsc(Long usuarioId, Long exercicioId);

	List<RecordePessoal> findByUsuarioIdAndExercicioIdIn(Long usuarioId, Collection<Long> exercicioIds);
}
