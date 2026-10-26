package br.edu.utfpr.ironquest.treino.domain;

/** Ciclo de vida da sessão: só sessões finalizadas geram recordes e progressão. */
public enum StatusSessao {
	EM_ANDAMENTO,
	FINALIZADA,
	CANCELADA
}
