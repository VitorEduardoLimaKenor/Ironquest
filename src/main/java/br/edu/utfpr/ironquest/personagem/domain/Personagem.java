package br.edu.utfpr.ironquest.personagem.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.edu.utfpr.ironquest.shared.domain.EntidadeBase;
import br.edu.utfpr.ironquest.usuario.domain.Usuario;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "personagem")
public class Personagem extends EntidadeBase {

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "usuario_id", nullable = false, unique = true)
	private Usuario usuario;

	@Column(nullable = false, length = 50)
	private String nome;

	@Enumerated(EnumType.STRING)
	@Column(name = "objetivo_principal", nullable = false, length = 20)
	private ObjetivoPessoal objetivoPrincipal;

	@Column(name = "frequencia_semanal_alvo", nullable = false)
	private int frequenciaSemanalAlvo;

	@Column(nullable = false)
	private int nivel = 1;

	@Column(name = "experiencia_total", nullable = false)
	private long experienciaTotal;

	@Column(name = "sequencia_atual", nullable = false)
	private int sequenciaAtual;

	@Column(name = "maior_sequencia", nullable = false)
	private int maiorSequencia;

	@Column(name = "ultimo_treino_em")
	private LocalDate ultimoTreinoEm;

	@OneToMany(mappedBy = "personagem", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Atributo> atributos = new ArrayList<>();

	protected Personagem() {
	}

	/** Cria o personagem no nível 1 com todos os atributos iniciais. */
	public Personagem(Usuario usuario, String nome, ObjetivoPessoal objetivoPrincipal, int frequenciaSemanalAlvo) {
		this.usuario = usuario;
		definirPerfil(nome, objetivoPrincipal, frequenciaSemanalAlvo);
		for (TipoAtributo tipo : TipoAtributo.values()) {
			atributos.add(new Atributo(this, tipo));
		}
	}

	public void definirPerfil(String nome, ObjetivoPessoal objetivoPrincipal, int frequenciaSemanalAlvo) {
		this.nome = nome;
		this.objetivoPrincipal = objetivoPrincipal;
		this.frequenciaSemanalAlvo = frequenciaSemanalAlvo;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public String getNome() {
		return nome;
	}

	public ObjetivoPessoal getObjetivoPrincipal() {
		return objetivoPrincipal;
	}

	public int getFrequenciaSemanalAlvo() {
		return frequenciaSemanalAlvo;
	}

	public int getNivel() {
		return nivel;
	}

	public long getExperienciaTotal() {
		return experienciaTotal;
	}

	public int getSequenciaAtual() {
		return sequenciaAtual;
	}

	public int getMaiorSequencia() {
		return maiorSequencia;
	}

	public LocalDate getUltimoTreinoEm() {
		return ultimoTreinoEm;
	}

	public List<Atributo> getAtributos() {
		List<Atributo> ordenados = new ArrayList<>(atributos);
		ordenados.sort(Comparator.comparing(Atributo::getTipo));
		return Collections.unmodifiableList(ordenados);
	}
}
