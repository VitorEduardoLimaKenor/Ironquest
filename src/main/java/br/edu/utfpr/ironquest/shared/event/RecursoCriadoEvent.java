package br.edu.utfpr.ironquest.shared.event;

import org.springframework.context.ApplicationEvent;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Publicado pelos controllers ao criar um recurso. O {@link RecursoCriadoListener} preenche o cabeçalho
 * {@code Location} da resposta.
 */
public class RecursoCriadoEvent extends ApplicationEvent {

	private final transient HttpServletResponse response;
	private final String caminhoRecurso;
	private final Long id;

	/** Location = URI da requisição atual + "/{id}" (ex.: POST /api/usuarios gera /api/usuarios/7). */
	public RecursoCriadoEvent(Object source, HttpServletResponse response, Long id) {
		this(source, response, null, id);
	}

	/**
	 * Location = caminho informado + "/{id}". Usado quando o recurso nasce em rota aninhada
	 * (ex.: POST /api/usuarios/1/personagem gera /api/personagens/3).
	 */
	public RecursoCriadoEvent(Object source, HttpServletResponse response, String caminhoRecurso, Long id) {
		super(source);
		this.response = response;
		this.caminhoRecurso = caminhoRecurso;
		this.id = id;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public String getCaminhoRecurso() {
		return caminhoRecurso;
	}

	public Long getId() {
		return id;
	}
}
