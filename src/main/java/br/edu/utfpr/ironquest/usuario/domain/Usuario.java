package br.edu.utfpr.ironquest.usuario.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import br.edu.utfpr.ironquest.shared.domain.EntidadeBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario extends EntidadeBase {

	@Column(nullable = false, length = 100)
	private String nome;

	@Column(nullable = false, unique = true, length = 150)
	private String email;

	@Column(name = "senha_hash", nullable = false, length = 100)
	private String senhaHash;

	@Column(nullable = false)
	private boolean ativo = true;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "usuario_papel",
			joinColumns = @JoinColumn(name = "usuario_id"),
			inverseJoinColumns = @JoinColumn(name = "papel_id"))
	private Set<Papel> papeis = new HashSet<>();

	protected Usuario() {
	}

	public Usuario(String nome, String email, String senhaHash) {
		this.nome = nome;
		this.email = email;
		this.senhaHash = senhaHash;
	}

	public void atualizar(String nome, String email) {
		this.nome = nome;
		this.email = email;
	}

	public void adicionarPapel(Papel papel) {
		papeis.add(papel);
	}

	public void definirAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Regra: só usuário ativo registra treinos e cria dados novos. */
	public void exigirAtivo() {
		if (!ativo) {
			throw new UsuarioInativoException(getId());
		}
	}

	public boolean possuiPapel(NomePapel nomePapel) {
		return papeis.stream().anyMatch(papel -> papel.getNome() == nomePapel);
	}

	public String getNome() {
		return nome;
	}

	public String getEmail() {
		return email;
	}

	public String getSenhaHash() {
		return senhaHash;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public Set<Papel> getPapeis() {
		return Collections.unmodifiableSet(papeis);
	}
}
