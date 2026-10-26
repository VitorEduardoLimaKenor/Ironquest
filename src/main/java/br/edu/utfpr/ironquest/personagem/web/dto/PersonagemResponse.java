package br.edu.utfpr.ironquest.personagem.web.dto;

import java.time.LocalDate;
import java.util.List;

import br.edu.utfpr.ironquest.personagem.domain.Atributo;
import br.edu.utfpr.ironquest.personagem.domain.ObjetivoPessoal;
import br.edu.utfpr.ironquest.personagem.domain.Personagem;
import br.edu.utfpr.ironquest.personagem.domain.TipoAtributo;
import br.edu.utfpr.ironquest.shared.web.dto.ReferenciaResponse;

public record PersonagemResponse(
		Long id,
		ReferenciaResponse usuario,
		String nome,
		ObjetivoPessoal objetivoPrincipal,
		int frequenciaSemanalAlvo,
		int nivel,
		long experienciaTotal,
		int sequenciaAtual,
		int maiorSequencia,
		LocalDate ultimoTreinoEm,
		List<AtributoResponse> atributos) {

	public record AtributoResponse(TipoAtributo tipo, int nivel, int experiencia) {

		static AtributoResponse de(Atributo atributo) {
			return new AtributoResponse(atributo.getTipo(), atributo.getNivel(), atributo.getExperiencia());
		}
	}

	public static PersonagemResponse de(Personagem personagem) {
		return new PersonagemResponse(
				personagem.getId(),
				new ReferenciaResponse(personagem.getUsuario().getId(), personagem.getUsuario().getNome()),
				personagem.getNome(),
				personagem.getObjetivoPrincipal(),
				personagem.getFrequenciaSemanalAlvo(),
				personagem.getNivel(),
				personagem.getExperienciaTotal(),
				personagem.getSequenciaAtual(),
				personagem.getMaiorSequencia(),
				personagem.getUltimoTreinoEm(),
				personagem.getAtributos().stream().map(AtributoResponse::de).toList());
	}
}
