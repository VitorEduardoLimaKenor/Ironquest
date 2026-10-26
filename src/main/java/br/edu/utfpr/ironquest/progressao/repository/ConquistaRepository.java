package br.edu.utfpr.ironquest.progressao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.utfpr.ironquest.progressao.domain.Conquista;

public interface ConquistaRepository extends JpaRepository<Conquista, Long> {

	@EntityGraph(attributePaths = "recompensa")
	List<Conquista> findAllByOrderByNomeAsc();

	@Override
	@EntityGraph(attributePaths = "recompensa")
	Optional<Conquista> findById(Long id);

	boolean existsByCodigo(String codigo);

	boolean existsByCodigoAndIdNot(String codigo, Long id);
}
