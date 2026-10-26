package br.edu.utfpr.ironquest.usuario.web;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.edu.utfpr.ironquest.shared.event.RecursoCriadoEvent;
import br.edu.utfpr.ironquest.usuario.service.UsuarioService;
import br.edu.utfpr.ironquest.usuario.web.dto.AtualizarUsuarioRequest;
import br.edu.utfpr.ironquest.usuario.web.dto.CriarUsuarioRequest;
import br.edu.utfpr.ironquest.usuario.web.dto.PapelResponse;
import br.edu.utfpr.ironquest.usuario.web.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Tag(name = "Usuários", description = "Cadastro de usuários e papéis (RBAC)")
public class UsuarioController {

	private final UsuarioService usuarioService;
	private final ApplicationEventPublisher publisher;

	public UsuarioController(UsuarioService usuarioService, ApplicationEventPublisher publisher) {
		this.usuarioService = usuarioService;
		this.publisher = publisher;
	}

	@GetMapping("/usuarios")
	@Operation(summary = "Lista usuários; filtro opcional por ativo")
	public List<UsuarioResponse> listar(@RequestParam(required = false) Boolean ativo) {
		return usuarioService.listar(ativo);
	}

	@GetMapping("/usuarios/{id}")
	public UsuarioResponse buscar(@PathVariable Long id) {
		return usuarioService.buscar(id);
	}

	@PostMapping("/usuarios")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Cadastra usuário (senha armazenada com BCrypt)")
	public UsuarioResponse criar(@Valid @RequestBody CriarUsuarioRequest request, HttpServletResponse response) {
		UsuarioResponse criado = usuarioService.criar(request);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, criado.id()));
		return criado;
	}

	@PutMapping("/usuarios/{id}")
	public UsuarioResponse atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarUsuarioRequest request) {
		return usuarioService.atualizar(id, request);
	}

	@PutMapping("/usuarios/{id}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Ativa ou desativa o usuário (atualização parcial; corpo: true ou false)")
	public void atualizarAtivo(@PathVariable Long id, @RequestBody Boolean ativo) {
		usuarioService.atualizarAtivo(id, ativo);
	}

	@DeleteMapping("/usuarios/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Exclui a conta e todo o histórico do usuário")
	public void excluir(@PathVariable Long id) {
		usuarioService.excluir(id);
	}

	@GetMapping("/papeis")
	@Operation(summary = "Lista papéis disponíveis")
	public List<PapelResponse> listarPapeis() {
		return usuarioService.listarPapeis();
	}
}
