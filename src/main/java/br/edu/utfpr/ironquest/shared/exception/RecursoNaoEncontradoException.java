package br.edu.utfpr.ironquest.shared.exception;

/** Recurso inexistente. Mapeada para HTTP 404. */
public class RecursoNaoEncontradoException extends IronQuestException {

	public RecursoNaoEncontradoException(String recurso, Object id) {
		super("recurso.nao-encontrado", recurso, id);
	}
}
