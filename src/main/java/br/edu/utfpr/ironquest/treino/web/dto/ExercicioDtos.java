package br.edu.utfpr.ironquest.treino.web.dto;

import br.edu.utfpr.ironquest.shared.web.dto.ReferenciaResponse;
import br.edu.utfpr.ironquest.treino.domain.Equipamento;
import br.edu.utfpr.ironquest.treino.domain.Exercicio;
import br.edu.utfpr.ironquest.treino.domain.TipoExercicio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** DTOs do catálogo de exercícios. */
public final class ExercicioDtos {

	private ExercicioDtos() {
	}

	public record ExercicioRequest(
			@NotBlank @Size(max = 100) String nome,
			@Size(max = 500) String descricao,
			@NotNull Long grupoMuscularId,
			@NotNull TipoExercicio tipo,
			@NotNull Equipamento equipamento) {
	}

	public record ExercicioResponse(
			Long id,
			String nome,
			String descricao,
			ReferenciaResponse grupoMuscular,
			TipoExercicio tipo,
			Equipamento equipamento,
			boolean ativo) {

		public static ExercicioResponse de(Exercicio exercicio) {
			return new ExercicioResponse(exercicio.getId(), exercicio.getNome(), exercicio.getDescricao(),
					new ReferenciaResponse(exercicio.getGrupoMuscular().getId(), exercicio.getGrupoMuscular().getNome()),
					exercicio.getTipo(), exercicio.getEquipamento(), exercicio.isAtivo());
		}
	}
}
