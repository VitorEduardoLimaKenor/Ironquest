package br.edu.utfpr.ironquest.progressao.domain;

import br.edu.utfpr.ironquest.personagem.domain.TipoAtributo;
import br.edu.utfpr.ironquest.shared.domain.EntidadeBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/** Pacote de experiência e pontos de atributo concedido por conquistas e missões. */
@Entity
@Table(name = "recompensa")
public class Recompensa extends EntidadeBase {

	@Column(nullable = false, length = 100)
	private String nome;

	@Column
	private String descricao;

	@Column(nullable = false)
	private int experiencia;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_atributo", length = 20)
	private TipoAtributo tipoAtributo;

	@Column(name = "pontos_atributo", nullable = false)
	private int pontosAtributo;

	protected Recompensa() {
	}

	public Recompensa(String nome, String descricao, int experiencia, TipoAtributo tipoAtributo, int pontosAtributo) {
		atualizar(nome, descricao, experiencia, tipoAtributo, pontosAtributo);
	}

	public void atualizar(String nome, String descricao, int experiencia, TipoAtributo tipoAtributo,
			int pontosAtributo) {
		this.nome = nome;
		this.descricao = descricao;
		this.experiencia = experiencia;
		this.tipoAtributo = tipoAtributo;
		this.pontosAtributo = pontosAtributo;
	}

	public String getNome() {
		return nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public int getExperiencia() {
		return experiencia;
	}

	public TipoAtributo getTipoAtributo() {
		return tipoAtributo;
	}

	public int getPontosAtributo() {
		return pontosAtributo;
	}
}
