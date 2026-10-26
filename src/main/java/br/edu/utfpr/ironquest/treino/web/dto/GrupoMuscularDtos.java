package br.edu.utfpr.ironquest.treino.web.dto;

import br.edu.utfpr.ironquest.treino.domain.GrupoMuscular;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTOs do catálogo de grupos musculares. */
public final class GrupoMuscularDtos {

	private GrupoMuscularDtos() {
	}

	public record GrupoMuscularRequest(
			@NotBlank @Size(max = 50) String nome,
			@Size(max = 255) String descricao) {
	}

	public record GrupoMuscularResponse(Long id, String nome, String descricao) {

		public static GrupoMuscularResponse de(GrupoMuscular grupo) {
			return new GrupoMuscularResponse(grupo.getId(), grupo.getNome(), grupo.getDescricao());
		}
	}
}
