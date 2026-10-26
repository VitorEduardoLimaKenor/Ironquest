package br.edu.utfpr.ironquest.treino.domain;

import br.edu.utfpr.ironquest.shared.domain.EntidadeBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "grupo_muscular")
public class GrupoMuscular extends EntidadeBase {

	@Column(nullable = false, unique = true, length = 50)
	private String nome;

	@Column
	private String descricao;

	protected GrupoMuscular() {
	}

	public GrupoMuscular(String nome, String descricao) {
		atualizar(nome, descricao);
	}

	public void atualizar(String nome, String descricao) {
		this.nome = nome;
		this.descricao = descricao;
	}

	public String getNome() {
		return nome;
	}

	public String getDescricao() {
		return descricao;
	}
}
