package br.edu.utfpr.ironquest.treino.repository.sessao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.edu.utfpr.ironquest.treino.domain.SessaoTreino;
import br.edu.utfpr.ironquest.treino.repository.filter.SessaoTreinoFilter;

/** Consulta customizada do repositório de sessões, implementada com Criteria API. */
public interface SessaoTreinoRepositoryQuery {

	Page<SessaoTreino> filtrar(SessaoTreinoFilter filtro, Pageable pageable);
}
