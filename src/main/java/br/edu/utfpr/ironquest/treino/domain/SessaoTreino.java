package br.edu.utfpr.ironquest.treino.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.edu.utfpr.ironquest.shared.domain.EntidadeBase;
import br.edu.utfpr.ironquest.shared.exception.RecursoNaoEncontradoException;
import br.edu.utfpr.ironquest.shared.exception.RegraNegocioException;
import br.edu.utfpr.ironquest.usuario.domain.Usuario;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * Sessão de treino realizada. Séries só podem ser alteradas enquanto a sessão está em andamento, e somente por
 * usuário ativo.
 */
@Entity
@Table(name = "sessao_treino")
public class SessaoTreino extends EntidadeBase {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "plano_treino_id")
	private PlanoTreino planoTreino;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private StatusSessao status = StatusSessao.EM_ANDAMENTO;

	@Column(name = "iniciada_em", nullable = false)
	private Instant iniciadaEm;

	/** Dia do treino no fuso do jogador; base dos filtros por período e do histórico diário. */
	@Column(name = "data_treino", nullable = false)
	private LocalDate dataTreino;

	@Column(name = "finalizada_em")
	private Instant finalizadaEm;

	@Column(name = "volume_total_kg", nullable = false, precision = 12, scale = 2)
	private BigDecimal volumeTotalKg = BigDecimal.ZERO.setScale(2);

	@Column(length = 500)
	private String observacoes;

	/** Bloqueio otimista: duas finalizações simultâneas não passam juntas. */
	@Version
	@Column(nullable = false)
	private Long versao;

	@OneToMany(mappedBy = "sessaoTreino", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("ordem ASC")
	private List<Serie> series = new ArrayList<>();

	protected SessaoTreino() {
	}

	public SessaoTreino(Usuario usuario, PlanoTreino planoTreino, Instant iniciadaEm, LocalDate dataTreino,
			String observacoes) {
		usuario.exigirAtivo();
		this.usuario = usuario;
		this.planoTreino = planoTreino;
		this.iniciadaEm = iniciadaEm;
		this.dataTreino = dataTreino;
		this.observacoes = observacoes;
	}

	public Serie adicionarSerie(Exercicio exercicio, TipoSerie tipo, int repeticoes, BigDecimal cargaKg,
			BigDecimal rpe, Integer rir) {
		exigirRegistroPermitido();
		int proximaOrdem = series.stream().mapToInt(Serie::getOrdem).max().orElse(0) + 1;
		Serie serie = new Serie(this, exercicio, proximaOrdem, tipo, repeticoes, cargaKg, rpe, rir);
		series.add(serie);
		return serie;
	}

	public Serie atualizarSerie(Long serieId, Exercicio exercicio, TipoSerie tipo, int repeticoes,
			BigDecimal cargaKg, BigDecimal rpe, Integer rir) {
		exigirRegistroPermitido();
		Serie serie = buscarSerie(serieId);
		serie.atualizar(exercicio, tipo, repeticoes, cargaKg, rpe, rir);
		return serie;
	}

	public void removerSerie(Long serieId) {
		exigirEmAndamento();
		series.remove(buscarSerie(serieId));
	}

	public Serie buscarSerie(Long serieId) {
		return series.stream()
				.filter(serie -> serieId.equals(serie.getId()))
				.findFirst()
				.orElseThrow(() -> new RecursoNaoEncontradoException("Série", serieId));
	}

	/** Fecha a sessão e consolida o volume total das séries de trabalho. */
	public void finalizar(Instant momento) {
		exigirRegistroPermitido();
		if (series.stream().noneMatch(Serie::isTrabalho)) {
			throw new RegraNegocioException("sessao.sem-series-de-trabalho");
		}
		if (momento.isBefore(iniciadaEm)) {
			throw new RegraNegocioException("sessao.finalizacao-antes-do-inicio");
		}
		this.volumeTotalKg = series.stream()
				.filter(Serie::isTrabalho)
				.map(Serie::getVolumeKg)
				.reduce(BigDecimal.ZERO, BigDecimal::add)
				.setScale(2, RoundingMode.HALF_UP);
		this.finalizadaEm = momento;
		this.status = StatusSessao.FINALIZADA;
	}

	public void cancelar() {
		exigirEmAndamento();
		this.status = StatusSessao.CANCELADA;
	}

	/** Sessões finalizadas geram recordes e compõem o histórico: não podem ser excluídas. */
	public void exigirExclusaoPermitida() {
		if (isFinalizada()) {
			throw new RegraNegocioException("sessao.finalizada-nao-pode-ser-excluida", getId());
		}
	}

	public boolean isFinalizada() {
		return status == StatusSessao.FINALIZADA;
	}

	/** Registrar resultados exige sessão em andamento e usuário ativo. */
	private void exigirRegistroPermitido() {
		exigirEmAndamento();
		usuario.exigirAtivo();
	}

	private void exigirEmAndamento() {
		if (status != StatusSessao.EM_ANDAMENTO) {
			throw new RegraNegocioException("sessao.nao-esta-em-andamento", getId(), status);
		}
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public PlanoTreino getPlanoTreino() {
		return planoTreino;
	}

	public StatusSessao getStatus() {
		return status;
	}

	public Instant getIniciadaEm() {
		return iniciadaEm;
	}

	public LocalDate getDataTreino() {
		return dataTreino;
	}

	public Instant getFinalizadaEm() {
		return finalizadaEm;
	}

	public BigDecimal getVolumeTotalKg() {
		return volumeTotalKg;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public List<Serie> getSeries() {
		return Collections.unmodifiableList(series);
	}
}
