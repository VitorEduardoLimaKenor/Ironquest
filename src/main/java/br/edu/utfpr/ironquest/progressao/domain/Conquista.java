package br.edu.utfpr.ironquest.progressao.domain;

import br.edu.utfpr.ironquest.shared.domain.EntidadeBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Conquista do catálogo. A regra é descrita em texto para que o jogador entenda o critério. */
@Entity
@Table(name = "conquista")
public class Conquista extends EntidadeBase {

	@Column(nullable = false, unique = true, length = 50)
	private String codigo;

	@Column(nullable = false, length = 100)
	private String nome;

	@Column(nullable = false)
	private String descricao;

	@Column(nullable = false)
	private String regra;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recompensa_id")
	private Recompensa recompensa;

	protected Conquista() {
	}

	public Conquista(String codigo, String nome, String descricao, String regra, Recompensa recompensa) {
		atualizar(codigo, nome, descricao, regra, recompensa);
	}

	public void atualizar(String codigo, String nome, String descricao, String regra, Recompensa recompensa) {
		this.codigo = codigo;
		this.nome = nome;
		this.descricao = descricao;
		this.regra = regra;
		this.recompensa = recompensa;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getNome() {
		return nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getRegra() {
		return regra;
	}

	public Recompensa getRecompensa() {
		return recompensa;
	}
}
