package br.edu.utfpr.ironquest.usuario.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarUsuarioRequest(
		@NotBlank @Size(max = 100) String nome,
		@NotBlank @Email @Size(max = 150) String email) {
}
