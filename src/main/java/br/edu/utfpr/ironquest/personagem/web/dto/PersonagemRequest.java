package br.edu.utfpr.ironquest.personagem.web.dto;

import br.edu.utfpr.ironquest.personagem.domain.ObjetivoPessoal;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Criação e edição do personagem, incluindo os objetivos pessoais. */
public record PersonagemRequest(
		@NotBlank @Size(max = 50) String nome,
		@NotNull ObjetivoPessoal objetivoPrincipal,
		@NotNull @Min(1) @Max(7) Integer frequenciaSemanalAlvo) {
}
