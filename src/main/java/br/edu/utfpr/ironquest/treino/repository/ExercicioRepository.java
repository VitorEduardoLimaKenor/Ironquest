package br.edu.utfpr.ironquest.treino.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.edu.utfpr.ironquest.treino.domain.Exercicio;

public interface ExercicioRepository extends JpaRepository<Exercicio, Long> {

	/** Filtros opcionais: parâmetro nulo não restringe a consulta. */
	@Query("""
			select e from Exercicio e join fetch e.grupoMuscular g
			where (:grupoMuscularId is null or g.id = :grupoMuscularId)
			  and (:ativo is null or e.ativo = :ativo)
			order by e.nome""")
	List<Exercicio> pesquisar(Long grupoMuscularId, Boolean ativo);

	@Override
	@EntityGraph(attributePaths = "grupoMuscular")
	Optional<Exercicio> findById(Long id);

	boolean existsByNome(String nome);

	boolean existsByNomeAndIdNot(String nome, Long id);

	boolean existsByGrupoMuscularId(Long grupoMuscularId);
}
