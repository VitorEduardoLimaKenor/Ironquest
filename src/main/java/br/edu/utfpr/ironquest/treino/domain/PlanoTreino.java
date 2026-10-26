package br.edu.utfpr.ironquest.treino.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.edu.utfpr.ironquest.shared.domain.EntidadeBase;
import br.edu.utfpr.ironquest.usuario.domain.Usuario;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

/**
 * Ficha de treino do jogador. O autor é opcional (treinador que montou o plano).
 */
@Entity
@Table(name = "plano_treino")
public class PlanoTreino extends EntidadeBase {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "autor_id")
	private Usuario autor;

	@Column(nullable = false, length = 100)
	private String nome;

	@Column(length = 500)
	private String descricao;

	@Column(nullable = false)
	private boolean ativo = true;

	@OneToMany(mappedBy = "planoTreino", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("ordem ASC")
	private List<ItemPlanoTreino> itens = new ArrayList<>();

	protected PlanoTreino() {
	}

	public PlanoTreino(Usuario usuario, Usuario autor, String nome, String descricao) {
		this.usuario = usuario;
		atualizar(autor, nome, descricao, true);
	}

	public void atualizar(Usuario autor, String nome, String descricao, boolean ativo) {
		this.autor = autor;
		this.nome = nome;
		this.descricao = descricao;
		this.ativo = ativo;
	}

	public void definirAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Adiciona o exercício ao final da ficha. */
	public void adicionarItem(Exercicio exercicio, int seriesAlvo, int repeticoesAlvo, Integer descansoSegundos,
			String observacao) {
		itens.add(new ItemPlanoTreino(this, exercicio, itens.size() + 1, seriesAlvo, repeticoesAlvo,
				descansoSegundos, observacao));
	}

	public void limparItens() {
		itens.clear();
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public Usuario getAutor() {
		return autor;
	}

	public String getNome() {
		return nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public List<ItemPlanoTreino> getItens() {
		return Collections.unmodifiableList(itens);
	}
}
