package br.edu.utfpr.ironquest.usuario.web.dto;

import br.edu.utfpr.ironquest.usuario.domain.NomePapel;
import br.edu.utfpr.ironquest.usuario.domain.Papel;

public record PapelResponse(Long id, NomePapel nome, String descricao) {

	public static PapelResponse de(Papel papel) {
		return new PapelResponse(papel.getId(), papel.getNome(), papel.getDescricao());
	}
}
