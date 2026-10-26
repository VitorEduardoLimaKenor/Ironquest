package br.edu.utfpr.ironquest.usuario.web.dto;

import java.util.Set;

import br.edu.utfpr.ironquest.usuario.domain.NomePapel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados de cadastro. Sem papéis informados, o usuário recebe JOGADOR.
 */
public record CriarUsuarioRequest(
		@NotBlank @Size(max = 100) String nome,
		@NotBlank @Email @Size(max = 150) String email,
		// BCrypt considera no máximo 72 bytes.
		@NotBlank @Size(min = 8, max = 72) String senha,
		Set<NomePapel> papeis) {
}
