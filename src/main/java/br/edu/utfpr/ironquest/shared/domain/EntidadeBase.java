package br.edu.utfpr.ironquest.shared.domain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.hibernate.Hibernate;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * Superclasse das entidades: chave primária auto incremento e datas de auditoria em UTC.
 */
@MappedSuperclass
public abstract class EntidadeBase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "criado_em", nullable = false, updatable = false)
	private Instant criadoEm;

	@Column(name = "atualizado_em", nullable = false)
	private Instant atualizadoEm;

	@PrePersist
	protected void aoCriar() {
		Instant agora = agora();
		criadoEm = agora;
		atualizadoEm = agora;
	}

	@PreUpdate
	protected void aoAtualizar() {
		atualizadoEm = agora();
	}

	/** Instante atual truncado para microssegundos, a precisão do DATETIME(6) do MySQL. */
	protected static Instant agora() {
		return Instant.now().truncatedTo(ChronoUnit.MICROS);
	}

	public Long getId() {
		return id;
	}

	public Instant getCriadoEm() {
		return criadoEm;
	}

	public Instant getAtualizadoEm() {
		return atualizadoEm;
	}

	@Override
	public boolean equals(Object outro) {
		if (this == outro) {
			return true;
		}
		if (outro == null || Hibernate.getClass(this) != Hibernate.getClass(outro)) {
			return false;
		}
		EntidadeBase entidade = (EntidadeBase) outro;
		return id != null && id.equals(entidade.id);
	}

	@Override
	public int hashCode() {
		return Hibernate.getClass(this).hashCode();
	}
}
