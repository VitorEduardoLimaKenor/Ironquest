package br.edu.utfpr.ironquest.shared.exception;

/** Operação conflita com o estado atual (duplicidade, vínculo existente). Mapeada para HTTP 409. */
public class ConflitoException extends IronQuestException {

	public ConflitoException(String codigoMensagem, Object... argumentos) {
		super(codigoMensagem, argumentos);
	}
}
