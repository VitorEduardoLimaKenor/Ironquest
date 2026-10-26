package br.edu.utfpr.ironquest.personagem.web;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.edu.utfpr.ironquest.personagem.service.PersonagemService;
import br.edu.utfpr.ironquest.personagem.web.dto.PersonagemRequest;
import br.edu.utfpr.ironquest.personagem.web.dto.PersonagemResponse;
import br.edu.utfpr.ironquest.shared.event.RecursoCriadoEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Tag(name = "Personagens", description = "Personagem RPG e objetivos pessoais do jogador")
public class PersonagemController {

	private final PersonagemService personagemService;
	private final ApplicationEventPublisher publisher;

	public PersonagemController(PersonagemService personagemService, ApplicationEventPublisher publisher) {
		this.personagemService = personagemService;
		this.publisher = publisher;
	}

	@PostMapping("/usuarios/{usuarioId}/personagem")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Cria o personagem do usuário (um por usuário) com atributos iniciais")
	public PersonagemResponse criar(@PathVariable Long usuarioId, @Valid @RequestBody PersonagemRequest request,
			HttpServletResponse response) {
		PersonagemResponse criado = personagemService.criar(usuarioId, request);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, "/api/personagens", criado.id()));
		return criado;
	}

	@GetMapping("/usuarios/{usuarioId}/personagem")
	public PersonagemResponse buscarPorUsuario(@PathVariable Long usuarioId) {
		return personagemService.buscarPorUsuario(usuarioId);
	}

	@GetMapping("/personagens/{id}")
	public PersonagemResponse buscar(@PathVariable Long id) {
		return personagemService.buscar(id);
	}

	@PutMapping("/personagens/{id}")
	@Operation(summary = "Atualiza nome e objetivos pessoais do personagem")
	public PersonagemResponse atualizar(@PathVariable Long id, @Valid @RequestBody PersonagemRequest request) {
		return personagemService.atualizar(id, request);
	}
}
