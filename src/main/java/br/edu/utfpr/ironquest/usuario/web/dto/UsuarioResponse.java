package br.edu.utfpr.ironquest.usuario.web.dto;

import java.time.Instant;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import br.edu.utfpr.ironquest.usuario.domain.NomePapel;
import br.edu.utfpr.ironquest.usuario.domain.Papel;
import br.edu.utfpr.ironquest.usuario.domain.Usuario;

public record UsuarioResponse(
		Long id,
		String nome,
		String email,
		boolean ativo,
		Set<NomePapel> papeis,
		Instant criadoEm) {

	public static UsuarioResponse de(Usuario usuario) {
		Set<NomePapel> papeis = usuario.getPapeis().stream()
				.map(Papel::getNome)
				.collect(Collectors.toCollection(TreeSet::new));
		return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.isAtivo(),
				papeis, usuario.getCriadoEm());
	}
}
