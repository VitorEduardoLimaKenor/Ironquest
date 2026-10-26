package br.edu.utfpr.ironquest.treino.web;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.edu.utfpr.ironquest.shared.event.RecursoCriadoEvent;
import br.edu.utfpr.ironquest.treino.service.PlanoTreinoService;
import br.edu.utfpr.ironquest.treino.web.dto.PlanoTreinoDtos.PlanoTreinoRequest;
import br.edu.utfpr.ironquest.treino.web.dto.PlanoTreinoDtos.PlanoTreinoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Tag(name = "Planos de treino", description = "Fichas de treino do jogador")
public class PlanoTreinoController {

	private final PlanoTreinoService planoTreinoService;
	private final ApplicationEventPublisher publisher;

	public PlanoTreinoController(PlanoTreinoService planoTreinoService, ApplicationEventPublisher publisher) {
		this.planoTreinoService = planoTreinoService;
		this.publisher = publisher;
	}

	@GetMapping("/usuarios/{usuarioId}/planos-treino")
	public List<PlanoTreinoResponse> listar(@PathVariable Long usuarioId) {
		return planoTreinoService.listarDoUsuario(usuarioId);
	}

	@PostMapping("/usuarios/{usuarioId}/planos-treino")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Cria plano de treino; itens seguem a ordem da lista")
	public PlanoTreinoResponse criar(@PathVariable Long usuarioId, @Valid @RequestBody PlanoTreinoRequest request,
			HttpServletResponse response) {
		PlanoTreinoResponse criado = planoTreinoService.criar(usuarioId, request);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, "/api/planos-treino", criado.id()));
		return criado;
	}

	@GetMapping("/planos-treino/{id}")
	public PlanoTreinoResponse buscar(@PathVariable Long id) {
		return planoTreinoService.buscar(id);
	}

	@PutMapping("/planos-treino/{id}")
	@Operation(summary = "Atualiza plano; a lista de itens substitui a anterior")
	public PlanoTreinoResponse atualizar(@PathVariable Long id, @Valid @RequestBody PlanoTreinoRequest request) {
		return planoTreinoService.atualizar(id, request);
	}

	@PutMapping("/planos-treino/{id}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Ativa ou desativa o plano (atualização parcial; corpo: true ou false)")
	public void atualizarAtivo(@PathVariable Long id, @RequestBody Boolean ativo) {
		planoTreinoService.atualizarAtivo(id, ativo);
	}

	@DeleteMapping("/planos-treino/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Exclui plano; sessões antigas ficam sem plano vinculado")
	public void excluir(@PathVariable Long id) {
		planoTreinoService.excluir(id);
	}
}
