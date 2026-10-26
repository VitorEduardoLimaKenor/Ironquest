package br.edu.utfpr.ironquest.treino.domain;

import java.math.BigDecimal;
import java.time.Instant;

import br.edu.utfpr.ironquest.shared.domain.EntidadeBase;
import br.edu.utfpr.ironquest.usuario.domain.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Melhor marca atual do usuário em um exercício para um tipo de recorde. */
@Entity
@Table(name = "recorde_pessoal")
public class RecordePessoal extends EntidadeBase {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "exercicio_id", nullable = false)
	private Exercicio exercicio;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private TipoRecorde tipo;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal valor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serie_id")
	private Serie serie;

	@Column(name = "alcancado_em", nullable = false)
	private Instant alcancadoEm;

	protected RecordePessoal() {
	}

	public RecordePessoal(Usuario usuario, Exercicio exercicio, TipoRecorde tipo, BigDecimal valor, Serie serie,
			Instant alcancadoEm) {
		this.usuario = usuario;
		this.exercicio = exercicio;
		this.tipo = tipo;
		this.valor = valor;
		this.serie = serie;
		this.alcancadoEm = alcancadoEm;
	}

	/** Atualiza o recorde se o novo valor for estritamente maior. Retorna se houve superação. */
	public boolean superar(BigDecimal novoValor, Serie novaSerie, Instant momento) {
		if (novoValor.compareTo(valor) <= 0) {
			return false;
		}
		this.valor = novoValor;
		this.serie = novaSerie;
		this.alcancadoEm = momento;
		return true;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public Exercicio getExercicio() {
		return exercicio;
	}

	public TipoRecorde getTipo() {
		return tipo;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public Serie getSerie() {
		return serie;
	}

	public Instant getAlcancadoEm() {
		return alcancadoEm;
	}
}
