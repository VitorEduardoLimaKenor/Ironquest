package br.edu.utfpr.ironquest.treino.web;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
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
import br.edu.utfpr.ironquest.treino.repository.filter.SessaoTreinoFilter;
import br.edu.utfpr.ironquest.treino.service.RecordePessoalService;
import br.edu.utfpr.ironquest.treino.service.SessaoTreinoService;
import br.edu.utfpr.ironquest.treino.web.dto.SessaoTreinoDtos.FinalizacaoSessaoResponse;
import br.edu.utfpr.ironquest.treino.web.dto.SessaoTreinoDtos.FinalizarSessaoRequest;
import br.edu.utfpr.ironquest.treino.web.dto.SessaoTreinoDtos.IniciarSessaoRequest;
import br.edu.utfpr.ironquest.treino.web.dto.SessaoTreinoDtos.RecordePessoalResponse;
import br.edu.utfpr.ironquest.treino.web.dto.SessaoTreinoDtos.SerieRequest;
import br.edu.utfpr.ironquest.treino.web.dto.SessaoTreinoDtos.SerieResponse;
import br.edu.utfpr.ironquest.treino.web.dto.SessaoTreinoDtos.SessaoTreinoResponse;
import br.edu.utfpr.ironquest.treino.web.dto.SessaoTreinoDtos.SessaoTreinoResumoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Tag(name = "Sessões de treino", description = "Registro e pesquisa de sessões, séries e recordes pessoais")
public class SessaoTreinoController {

	private final SessaoTreinoService sessaoTreinoService;
	private final RecordePessoalService recordePessoalService;
	private final ApplicationEventPublisher publisher;

	public SessaoTreinoController(SessaoTreinoService sessaoTreinoService,
			RecordePessoalService recordePessoalService, ApplicationEventPublisher publisher) {
		this.sessaoTreinoService = sessaoTreinoService;
		this.recordePessoalService = recordePessoalService;
		this.publisher = publisher;
	}

	@GetMapping("/sessoes-treino")
	@Operation(summary = "Pesquisa sessões com filtros e paginação (page, size, sort)")
	public PagedModel<SessaoTreinoResumoResponse> pesquisar(@Valid @ParameterObject SessaoTreinoFilter filtro,
			@ParameterObject Pageable pageable) {
		return new PagedModel<>(sessaoTreinoService.pesquisar(filtro, pageable));
	}

	@GetMapping("/usuarios/{usuarioId}/sessoes-treino")
	@Operation(summary = "Histórico de sessões do usuário, com os mesmos filtros e paginação")
	public PagedModel<SessaoTreinoResumoResponse> pesquisarDoUsuario(@PathVariable Long usuarioId,
			@Valid @ParameterObject SessaoTreinoFilter filtro, @ParameterObject Pageable pageable) {
		return new PagedModel<>(sessaoTreinoService.pesquisarDoUsuario(usuarioId, filtro, pageable));
	}

	@PostMapping("/usuarios/{usuarioId}/sessoes-treino")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Inicia sessão de treino (uma em andamento por usuário ativo)")
	public SessaoTreinoResponse iniciar(@PathVariable Long usuarioId, @Valid @RequestBody IniciarSessaoRequest request,
			HttpServletResponse response) {
		SessaoTreinoResponse criada = sessaoTreinoService.iniciar(usuarioId, request);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, "/api/sessoes-treino", criada.id()));
		return criada;
	}

	@GetMapping("/sessoes-treino/{id}")
	public SessaoTreinoResponse buscar(@PathVariable Long id) {
		return sessaoTreinoService.buscar(id);
	}

	@DeleteMapping("/sessoes-treino/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Exclui sessão em andamento ou cancelada (finalizada responde 422)")
	public void excluir(@PathVariable Long id) {
		sessaoTreinoService.excluir(id);
	}

	@PostMapping("/sessoes-treino/{id}/series")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Registra série; a API calcula volume e 1RM estimado (Epley)")
	public SerieResponse adicionarSerie(@PathVariable Long id, @Valid @RequestBody SerieRequest request,
			HttpServletResponse response) {
		SerieResponse criada = sessaoTreinoService.adicionarSerie(id, request);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, criada.id()));
		return criada;
	}

	@GetMapping("/sessoes-treino/{id}/series/{serieId}")
	public SerieResponse buscarSerie(@PathVariable Long id, @PathVariable Long serieId) {
		return sessaoTreinoService.buscarSerie(id, serieId);
	}

	@PutMapping("/sessoes-treino/{id}/series/{serieId}")
	@Operation(summary = "Corrige série da sessão em andamento e recalcula volume e 1RM")
	public SerieResponse atualizarSerie(@PathVariable Long id, @PathVariable Long serieId,
			@Valid @RequestBody SerieRequest request) {
		return sessaoTreinoService.atualizarSerie(id, serieId, request);
	}

	@DeleteMapping("/sessoes-treino/{id}/series/{serieId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removerSerie(@PathVariable Long id, @PathVariable Long serieId) {
		sessaoTreinoService.removerSerie(id, serieId);
	}

	@PostMapping("/sessoes-treino/{id}/finalizar")
	@Operation(summary = "Finaliza sessão e atualiza recordes (idempotente em reenvios)")
	public FinalizacaoSessaoResponse finalizar(@PathVariable Long id,
			@Valid @RequestBody(required = false) FinalizarSessaoRequest request) {
		return sessaoTreinoService.finalizar(id, request);
	}

	@PostMapping("/sessoes-treino/{id}/cancelar")
	public SessaoTreinoResponse cancelar(@PathVariable Long id) {
		return sessaoTreinoService.cancelar(id);
	}

	@GetMapping("/usuarios/{usuarioId}/recordes")
	@Operation(summary = "Recordes pessoais do usuário, com filtro opcional por exercício")
	public List<RecordePessoalResponse> listarRecordes(@PathVariable Long usuarioId,
			@RequestParam(required = false) Long exercicioId) {
		return recordePessoalService.listar(usuarioId, exercicioId);
	}
}
