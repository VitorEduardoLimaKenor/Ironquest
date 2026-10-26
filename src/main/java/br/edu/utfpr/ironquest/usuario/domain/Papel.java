package br.edu.utfpr.ironquest.usuario.domain;

import br.edu.utfpr.ironquest.shared.domain.EntidadeBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "papel")
public class Papel extends EntidadeBase {

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, unique = true, length = 30)
	private NomePapel nome;

	@Column(nullable = false)
	private String descricao;

	protected Papel() {
	}

	public NomePapel getNome() {
		return nome;
	}

	public String getDescricao() {
		return descricao;
	}
}
