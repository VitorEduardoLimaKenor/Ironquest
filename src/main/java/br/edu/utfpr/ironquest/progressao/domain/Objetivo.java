package br.edu.utfpr.ironquest.progressao.domain;

import java.math.BigDecimal;

import br.edu.utfpr.ironquest.shared.domain.EntidadeBase;
import br.edu.utfpr.ironquest.treino.domain.Exercicio;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Meta mensurável de uma missão. O exercício é opcional (métricas globais não exigem). */
@Entity
@Table(name = "objetivo")
public class Objetivo extends EntidadeBase {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "missao_id", nullable = false)
	private Missao missao;

	@Column(nullable = false)
	private String descricao;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private MetricaObjetivo metrica;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "exercicio_id")
	private Exercicio exercicio;

	@Column(name = "valor_alvo", nullable = false, precision = 12, scale = 2)
	private BigDecimal valorAlvo;

	@Column(name = "valor_atual", nullable = false, precision = 12, scale = 2)
	private BigDecimal valorAtual = BigDecimal.ZERO.setScale(2);

	@Column(nullable = false)
	private boolean concluido;

	protected Objetivo() {
	}

	Objetivo(Missao missao, String descricao, MetricaObjetivo metrica, Exercicio exercicio, BigDecimal valorAlvo) {
		this.missao = missao;
		this.descricao = descricao;
		this.metrica = metrica;
		this.exercicio = exercicio;
		this.valorAlvo = valorAlvo;
	}

	public Missao getMissao() {
		return missao;
	}

	public String getDescricao() {
		return descricao;
	}

	public MetricaObjetivo getMetrica() {
		return metrica;
	}

	public Exercicio getExercicio() {
		return exercicio;
	}

	public BigDecimal getValorAlvo() {
		return valorAlvo;
	}

	public BigDecimal getValorAtual() {
		return valorAtual;
	}

	public boolean isConcluido() {
		return concluido;
	}
}
