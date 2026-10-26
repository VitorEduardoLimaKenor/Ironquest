package br.edu.utfpr.ironquest.shared.exception;

/** Requisição válida em formato, mas que viola uma regra de domínio. Mapeada para HTTP 422. */
public class RegraNegocioException extends IronQuestException {

	public RegraNegocioException(String codigoMensagem, Object... argumentos) {
		super(codigoMensagem, argumentos);
	}
}
