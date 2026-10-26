package br.edu.utfpr.ironquest.shared.exception;

/** Parâmetro de consulta inválido (ex.: ordenação por campo não permitido). Mapeada para HTTP 400. */
public class ParametroInvalidoException extends IronQuestException {

	public ParametroInvalidoException(String codigoMensagem, Object... argumentos) {
		super(codigoMensagem, argumentos);
	}
}
