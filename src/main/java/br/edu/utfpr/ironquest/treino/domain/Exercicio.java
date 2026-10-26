package br.edu.utfpr.ironquest.treino.domain;

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
@Table(name = "exercicio")
public class Exercicio extends EntidadeBase {

	@Column(nullable = false, unique = true, length = 100)
	private String nome;

	@Column(length = 500)
	private String descricao;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "grupo_muscular_id", nullable = false)
	private GrupoMuscular grupoMuscular;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TipoExercicio tipo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Equipamento equipamento;

	@Column(nullable = false)
	private boolean ativo = true;

	protected Exercicio() {
	}

	public Exercicio(String nome, String descricao, GrupoMuscular grupoMuscular, TipoExercicio tipo,
			Equipamento equipamento) {
		atualizar(nome, descricao, grupoMuscular, tipo, equipamento);
	}

	public void atualizar(String nome, String descricao, GrupoMuscular grupoMuscular, TipoExercicio tipo,
			Equipamento equipamento) {
		this.nome = nome;
		this.descricao = descricao;
		this.grupoMuscular = grupoMuscular;
		this.tipo = tipo;
		this.equipamento = equipamento;
	}

	/** Exercício inativo some do catálogo para novos registros, mas o histórico continua apontando para ele. */
	public void definirAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getNome() {
		return nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public GrupoMuscular getGrupoMuscular() {
		return grupoMuscular;
	}

	public TipoExercicio getTipo() {
		return tipo;
	}

	public Equipamento getEquipamento() {
		return equipamento;
	}

	public boolean isAtivo() {
		return ativo;
	}
}
