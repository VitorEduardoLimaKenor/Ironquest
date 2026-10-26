package br.edu.utfpr.ironquest.progressao.domain;

import java.time.Instant;

import br.edu.utfpr.ironquest.personagem.domain.Personagem;
import br.edu.utfpr.ironquest.shared.domain.EntidadeBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Conquista obtida por um personagem. A restrição única do banco impede concessão duplicada. */
@Entity
@Table(name = "conquista_personagem")
public class ConquistaPersonagem extends EntidadeBase {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "personagem_id", nullable = false)
	private Personagem personagem;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "conquista_id", nullable = false)
	private Conquista conquista;

	@Column(name = "obtida_em", nullable = false)
	private Instant obtidaEm;

	protected ConquistaPersonagem() {
	}

	public ConquistaPersonagem(Personagem personagem, Conquista conquista, Instant obtidaEm) {
		this.personagem = personagem;
		this.conquista = conquista;
		this.obtidaEm = obtidaEm;
	}

	public Personagem getPersonagem() {
		return personagem;
	}

	public Conquista getConquista() {
		return conquista;
	}

	public Instant getObtidaEm() {
		return obtidaEm;
	}
}
