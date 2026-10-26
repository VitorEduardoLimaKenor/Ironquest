package br.edu.utfpr.ironquest.personagem.domain;

import br.edu.utfpr.ironquest.shared.domain.EntidadeBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "atributo")
public class Atributo extends EntidadeBase {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "personagem_id", nullable = false)
	private Personagem personagem;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TipoAtributo tipo;

	@Column(nullable = false)
	private int nivel = 1;

	@Column(nullable = false)
	private int experiencia;

	protected Atributo() {
	}

	Atributo(Personagem personagem, TipoAtributo tipo) {
		this.personagem = personagem;
		this.tipo = tipo;
	}

	public Personagem getPersonagem() {
		return personagem;
	}

	public TipoAtributo getTipo() {
		return tipo;
	}

	public int getNivel() {
		return nivel;
	}

	public int getExperiencia() {
		return experiencia;
	}
}
