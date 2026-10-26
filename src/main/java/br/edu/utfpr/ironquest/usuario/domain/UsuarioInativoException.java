package br.edu.utfpr.ironquest.usuario.domain;

import br.edu.utfpr.ironquest.shared.exception.RegraNegocioException;

/**
 * Usuário desativado tentando registrar dados novos (sessão, série, plano, personagem). Mapeada para HTTP 422.
 */
public class UsuarioInativoException extends RegraNegocioException {

	public UsuarioInativoException(Long usuarioId) {
		super("usuario.inativo", usuarioId);
	}
}
