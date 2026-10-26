package br.edu.utfpr.ironquest.shared.exception;

import java.util.Arrays;

/**
 * Base das exceções de domínio. Carrega a chave da mensagem em {@code messages.properties} e seus argumentos;
 * o tratador global resolve o texto exibido ao usuário.
 */
public abstract class IronQuestException extends RuntimeException {

	private final String codigoMensagem;
	private final Object[] argumentos;

	protected IronQuestException(String codigoMensagem, Object... argumentos) {
		super(argumentos.length == 0 ? codigoMensagem : codigoMensagem + " " + Arrays.toString(argumentos));
		this.codigoMensagem = codigoMensagem;
		// Texto puro: evita que o MessageFormat formate ids como números ("1.234").
		this.argumentos = Arrays.stream(argumentos).map(String::valueOf).toArray();
	}

	public String getCodigoMensagem() {
		return codigoMensagem;
	}

	public Object[] getArgumentos() {
		return argumentos.clone();
	}
}
