package br.edu.utfpr.ironquest.progressao.domain;

import java.time.Instant;

import br.edu.utfpr.ironquest.personagem.domain.Personagem;
import br.edu.utfpr.ironquest.shared.domain.EntidadeBase;
import br.edu.utfpr.ironquest.treino.domain.SessaoTreino;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Registro imutável de cada recompensa concedida, com a regra que a explica.
 * A chave de idempotência é única no banco: reprocessar a mesma origem não premia duas vezes.
 */
@Entity
@Table(name = "evento_progressao")
public class EventoProgressao extends EntidadeBase {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "personagem_id", nullable = false)
	private Personagem personagem;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private TipoEvento tipo;

	@Column(nullable = false, length = 50)
	private String regra;

	@Column(nullable = false)
	private String descricao;

	@Column(nullable = false)
	private int experiencia;

	@Column(name = "chave_idempotencia", nullable = false, unique = true, length = 150)
	private String chaveIdempotencia;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sessao_treino_id")
	private SessaoTreino sessaoTreino;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "missao_id")
	private Missao missao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "conquista_id")
	private Conquista conquista;

	@Column(name = "ocorrido_em", nullable = false)
	private Instant ocorridoEm;

	protected EventoProgressao() {
	}

	public EventoProgressao(Personagem personagem, TipoEvento tipo, String regra, String descricao, int experiencia,
			String chaveIdempotencia, Instant ocorridoEm) {
		this.personagem = personagem;
		this.tipo = tipo;
		this.regra = regra;
		this.descricao = descricao;
		this.experiencia = experiencia;
		this.chaveIdempotencia = chaveIdempotencia;
		this.ocorridoEm = ocorridoEm;
	}

	public EventoProgressao originadoPor(SessaoTreino sessaoTreino, Missao missao, Conquista conquista) {
		this.sessaoTreino = sessaoTreino;
		this.missao = missao;
		this.conquista = conquista;
		return this;
	}

	public Personagem getPersonagem() {
		return personagem;
	}

	public TipoEvento getTipo() {
		return tipo;
	}

	public String getRegra() {
		return regra;
	}

	public String getDescricao() {
		return descricao;
	}

	public int getExperiencia() {
		return experiencia;
	}

	public String getChaveIdempotencia() {
		return chaveIdempotencia;
	}

	public SessaoTreino getSessaoTreino() {
		return sessaoTreino;
	}

	public Missao getMissao() {
		return missao;
	}

	public Conquista getConquista() {
		return conquista;
	}

	public Instant getOcorridoEm() {
		return ocorridoEm;
	}
}
