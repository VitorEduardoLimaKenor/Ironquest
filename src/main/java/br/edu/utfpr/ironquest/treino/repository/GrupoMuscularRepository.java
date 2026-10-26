package br.edu.utfpr.ironquest.treino.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.utfpr.ironquest.treino.domain.GrupoMuscular;

public interface GrupoMuscularRepository extends JpaRepository<GrupoMuscular, Long> {

	List<GrupoMuscular> findAllByOrderByNomeAsc();

	boolean existsByNome(String nome);

	boolean existsByNomeAndIdNot(String nome, Long id);
}
