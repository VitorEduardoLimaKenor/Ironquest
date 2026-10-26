package br.edu.utfpr.ironquest.treino.web.dto;

import java.util.List;

import br.edu.utfpr.ironquest.shared.web.dto.ReferenciaResponse;
import br.edu.utfpr.ironquest.treino.domain.ItemPlanoTreino;
import br.edu.utfpr.ironquest.treino.domain.PlanoTreino;
import br.edu.utfpr.ironquest.usuario.domain.Usuario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** DTOs de planos de treino (fichas). */
public final class PlanoTreinoDtos {

	private PlanoTreinoDtos() {
	}

	/**
	 * Itens são gravados na ordem da lista. Na edição, a lista substitui todos os itens anteriores.
	 */
	public record PlanoTreinoRequest(
			@NotBlank @Size(max = 100) String nome,
			@Size(max = 500) String descricao,
			Long autorId,
			Boolean ativo,
			@NotEmpty @Size(max = 30) List<@Valid @NotNull ItemPlanoTreinoRequest> itens) {
	}

	public record ItemPlanoTreinoRequest(
			@NotNull Long exercicioId,
			@NotNull @Min(1) @Max(20) Integer seriesAlvo,
			@NotNull @Min(1) @Max(100) Integer repeticoesAlvo,
			@Min(0) @Max(900) Integer descansoSegundos,
			@Size(max = 255) String observacao) {
	}

	public record PlanoTreinoResponse(
			Long id,
			ReferenciaResponse usuario,
			ReferenciaResponse autor,
			String nome,
			String descricao,
			boolean ativo,
			List<ItemPlanoTreinoResponse> itens) {

		public static PlanoTreinoResponse de(PlanoTreino plano) {
			return new PlanoTreinoResponse(plano.getId(), referencia(plano.getUsuario()),
					referencia(plano.getAutor()), plano.getNome(), plano.getDescricao(), plano.isAtivo(),
					plano.getItens().stream().map(ItemPlanoTreinoResponse::de).toList());
		}

		private static ReferenciaResponse referencia(Usuario usuario) {
			return usuario == null ? null : new ReferenciaResponse(usuario.getId(), usuario.getNome());
		}
	}

	public record ItemPlanoTreinoResponse(
			Long id,
			int ordem,
			ReferenciaResponse exercicio,
			int seriesAlvo,
			int repeticoesAlvo,
			Integer descansoSegundos,
			String observacao) {

		static ItemPlanoTreinoResponse de(ItemPlanoTreino item) {
			return new ItemPlanoTreinoResponse(item.getId(), item.getOrdem(),
					new ReferenciaResponse(item.getExercicio().getId(), item.getExercicio().getNome()),
					item.getSeriesAlvo(), item.getRepeticoesAlvo(), item.getDescansoSegundos(), item.getObservacao());
		}
	}
}
