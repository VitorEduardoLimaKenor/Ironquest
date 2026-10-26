package br.edu.utfpr.ironquest.progressao.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.edu.utfpr.ironquest.personagem.domain.Personagem;
import br.edu.utfpr.ironquest.shared.domain.EntidadeBase;
import br.edu.utfpr.ironquest.treino.domain.Exercicio;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Missão individual gerada a partir do histórico do jogador. Missões do tipo CHEFE têm pontos de vida.
 */
@Entity
@Table(name = "missao")
public class Missao extends EntidadeBase {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "personagem_id", nullable = false)
	private Personagem personagem;

	@Column(nullable = false, length = 100)
	private String titulo;

	@Column(length = 500)
	private String descricao;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TipoMissao tipo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private StatusMissao status = StatusMissao.ATIVA;

	@Column(name = "inicia_em", nullable = false)
	private Instant iniciaEm;

	@Column(name = "prazo_em", nullable = false)
	private Instant prazoEm;

	@Column(name = "concluida_em")
	private Instant concluidaEm;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recompensa_id")
	private Recompensa recompensa;

	@Column(name = "nome_chefe", length = 100)
	private String nomeChefe;

	@Column(name = "vida_total_chefe")
	private Integer vidaTotalChefe;

	@Column(name = "vida_restante_chefe")
	private Integer vidaRestanteChefe;

	@OneToMany(mappedBy = "missao", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Objetivo> objetivos = new ArrayList<>();

	protected Missao() {
	}

	public Missao(Personagem personagem, String titulo, String descricao, TipoMissao tipo, Instant iniciaEm,
			Instant prazoEm, Recompensa recompensa) {
		this.personagem = personagem;
		this.titulo = titulo;
		this.descricao = descricao;
		this.tipo = tipo;
		this.iniciaEm = iniciaEm;
		this.prazoEm = prazoEm;
		this.recompensa = recompensa;
	}

	public void definirChefe(String nomeChefe, int vidaTotal) {
		this.nomeChefe = nomeChefe;
		this.vidaTotalChefe = vidaTotal;
		this.vidaRestanteChefe = vidaTotal;
	}

	public Objetivo adicionarObjetivo(String descricao, MetricaObjetivo metrica, Exercicio exercicio,
			BigDecimal valorAlvo) {
		Objetivo objetivo = new Objetivo(this, descricao, metrica, exercicio, valorAlvo);
		objetivos.add(objetivo);
		return objetivo;
	}

	public Personagem getPersonagem() {
		return personagem;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public TipoMissao getTipo() {
		return tipo;
	}

	public StatusMissao getStatus() {
		return status;
	}

	public Instant getIniciaEm() {
		return iniciaEm;
	}

	public Instant getPrazoEm() {
		return prazoEm;
	}

	public Instant getConcluidaEm() {
		return concluidaEm;
	}

	public Recompensa getRecompensa() {
		return recompensa;
	}

	public String getNomeChefe() {
		return nomeChefe;
	}

	public Integer getVidaTotalChefe() {
		return vidaTotalChefe;
	}

	public Integer getVidaRestanteChefe() {
		return vidaRestanteChefe;
	}

	public List<Objetivo> getObjetivos() {
		return Collections.unmodifiableList(objetivos);
	}
}
