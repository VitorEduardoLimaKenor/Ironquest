package br.edu.utfpr.ironquest.treino.domain;

import br.edu.utfpr.ironquest.shared.domain.EntidadeBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Exercício prescrito dentro de um plano, com séries e repetições alvo. */
@Entity
@Table(name = "item_plano_treino")
public class ItemPlanoTreino extends EntidadeBase {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "plano_treino_id", nullable = false)
	private PlanoTreino planoTreino;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "exercicio_id", nullable = false)
	private Exercicio exercicio;

	@Column(nullable = false)
	private int ordem;

	@Column(name = "series_alvo", nullable = false)
	private int seriesAlvo;

	@Column(name = "repeticoes_alvo", nullable = false)
	private int repeticoesAlvo;

	@Column(name = "descanso_segundos")
	private Integer descansoSegundos;

	@Column
	private String observacao;

	protected ItemPlanoTreino() {
	}

	ItemPlanoTreino(PlanoTreino planoTreino, Exercicio exercicio, int ordem, int seriesAlvo, int repeticoesAlvo,
			Integer descansoSegundos, String observacao) {
		this.planoTreino = planoTreino;
		this.exercicio = exercicio;
		this.ordem = ordem;
		this.seriesAlvo = seriesAlvo;
		this.repeticoesAlvo = repeticoesAlvo;
		this.descansoSegundos = descansoSegundos;
		this.observacao = observacao;
	}

	public PlanoTreino getPlanoTreino() {
		return planoTreino;
	}

	public Exercicio getExercicio() {
		return exercicio;
	}

	public int getOrdem() {
		return ordem;
	}

	public int getSeriesAlvo() {
		return seriesAlvo;
	}

	public int getRepeticoesAlvo() {
		return repeticoesAlvo;
	}

	public Integer getDescansoSegundos() {
		return descansoSegundos;
	}

	public String getObservacao() {
		return observacao;
	}
}
