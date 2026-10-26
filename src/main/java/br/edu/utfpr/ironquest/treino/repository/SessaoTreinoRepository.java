package br.edu.utfpr.ironquest.treino.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.utfpr.ironquest.treino.domain.SessaoTreino;
import br.edu.utfpr.ironquest.treino.domain.StatusSessao;
import br.edu.utfpr.ironquest.treino.repository.sessao.SessaoTreinoRepositoryQuery;

public interface SessaoTreinoRepository extends JpaRepository<SessaoTreino, Long>, SessaoTreinoRepositoryQuery {

	@Override
	@EntityGraph(attributePaths = { "usuario", "planoTreino", "series", "series.exercicio" })
	Optional<SessaoTreino> findById(Long id);

	boolean existsByUsuarioIdAndStatus(Long usuarioId, StatusSessao status);
}
