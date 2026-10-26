package br.edu.utfpr.ironquest.treino.domain;

import java.math.BigDecimal;

import br.edu.utfpr.ironquest.shared.domain.EntidadeBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Série executada. Volume e 1RM estimado são calculados pela API, nunca enviados pelo cliente.
 */
@Entity
@Table(name = "serie")
public class Serie extends EntidadeBase {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "sessao_treino_id", nullable = false)
	private SessaoTreino sessaoTreino;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "exercicio_id", nullable = false)
	private Exercicio exercicio;

	@Column(nullable = false)
	private int ordem;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TipoSerie tipo;

	@Column(nullable = false)
	private int repeticoes;

	@Column(name = "carga_kg", nullable = false, precision = 6, scale = 2)
	private BigDecimal cargaKg;

	/** Percepção subjetiva de esforço (escala 1 a 10). */
	@Column(precision = 3, scale = 1)
	private BigDecimal rpe;

	/** Repetições em reserva. */
	@Column
	private Integer rir;

	@Column(name = "volume_kg", nullable = false, precision = 10, scale = 2)
	private BigDecimal volumeKg;

	@Column(name = "um_rm_estimado_kg", nullable = false, precision = 7, scale = 2)
	private BigDecimal umRmEstimadoKg;

	protected Serie() {
	}

	Serie(SessaoTreino sessaoTreino, Exercicio exercicio, int ordem, TipoSerie tipo, int repeticoes,
			BigDecimal cargaKg, BigDecimal rpe, Integer rir) {
		this.sessaoTreino = sessaoTreino;
		this.ordem = ordem;
		atualizar(exercicio, tipo, repeticoes, cargaKg, rpe, rir);
	}

	/** Corrige a série e recalcula volume e 1RM estimado. */
	void atualizar(Exercicio exercicio, TipoSerie tipo, int repeticoes, BigDecimal cargaKg, BigDecimal rpe,
			Integer rir) {
		this.exercicio = exercicio;
		this.tipo = tipo;
		this.repeticoes = repeticoes;
		this.cargaKg = cargaKg;
		this.rpe = rpe;
		this.rir = rir;
		this.volumeKg = CalculadoraDesempenho.volume(repeticoes, cargaKg);
		this.umRmEstimadoKg = CalculadoraDesempenho.umRmEstimado(repeticoes, cargaKg);
	}

	public boolean isTrabalho() {
		return tipo == TipoSerie.TRABALHO;
	}

	public SessaoTreino getSessaoTreino() {
		return sessaoTreino;
	}

	public Exercicio getExercicio() {
		return exercicio;
	}

	public int getOrdem() {
		return ordem;
	}

	public TipoSerie getTipo() {
		return tipo;
	}

	public int getRepeticoes() {
		return repeticoes;
	}

	public BigDecimal getCargaKg() {
		return cargaKg;
	}

	public BigDecimal getRpe() {
		return rpe;
	}

	public Integer getRir() {
		return rir;
	}

	public BigDecimal getVolumeKg() {
		return volumeKg;
	}

	public BigDecimal getUmRmEstimadoKg() {
		return umRmEstimadoKg;
	}
}
