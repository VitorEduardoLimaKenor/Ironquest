package br.edu.utfpr.ironquest.progressao.web;

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

import br.edu.utfpr.ironquest.progressao.service.ConquistaService;
import br.edu.utfpr.ironquest.progressao.service.RecompensaService;
import br.edu.utfpr.ironquest.progressao.web.dto.ProgressaoDtos.ConquistaRequest;
import br.edu.utfpr.ironquest.progressao.web.dto.ProgressaoDtos.ConquistaResponse;
import br.edu.utfpr.ironquest.progressao.web.dto.ProgressaoDtos.RecompensaRequest;
import br.edu.utfpr.ironquest.progressao.web.dto.ProgressaoDtos.RecompensaResponse;
import br.edu.utfpr.ironquest.shared.event.RecursoCriadoEvent;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Tag(name = "Catálogo de progressão", description = "Recompensas e conquistas disponíveis no jogo")
public class CatalogoProgressaoController {

	private final RecompensaService recompensaService;
	private final ConquistaService conquistaService;
	private final ApplicationEventPublisher publisher;

	public CatalogoProgressaoController(RecompensaService recompensaService, ConquistaService conquistaService,
			ApplicationEventPublisher publisher) {
		this.recompensaService = recompensaService;
		this.conquistaService = conquistaService;
		this.publisher = publisher;
	}

	@GetMapping("/recompensas")
	public List<RecompensaResponse> listarRecompensas() {
		return recompensaService.listar();
	}

	@GetMapping("/recompensas/{id}")
	public RecompensaResponse buscarRecompensa(@PathVariable Long id) {
		return recompensaService.buscar(id);
	}

	@PostMapping("/recompensas")
	@ResponseStatus(HttpStatus.CREATED)
	public RecompensaResponse criarRecompensa(@Valid @RequestBody RecompensaRequest request,
			HttpServletResponse response) {
		RecompensaResponse criada = recompensaService.criar(request);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, criada.id()));
		return criada;
	}

	@PutMapping("/recompensas/{id}")
	public RecompensaResponse atualizarRecompensa(@PathVariable Long id,
			@Valid @RequestBody RecompensaRequest request) {
		return recompensaService.atualizar(id, request);
	}

	@DeleteMapping("/recompensas/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluirRecompensa(@PathVariable Long id) {
		recompensaService.excluir(id);
	}

	@GetMapping("/conquistas")
	public List<ConquistaResponse> listarConquistas() {
		return conquistaService.listar();
	}

	@GetMapping("/conquistas/{id}")
	public ConquistaResponse buscarConquista(@PathVariable Long id) {
		return conquistaService.buscar(id);
	}

	@PostMapping("/conquistas")
	@ResponseStatus(HttpStatus.CREATED)
	public ConquistaResponse criarConquista(@Valid @RequestBody ConquistaRequest request,
			HttpServletResponse response) {
		ConquistaResponse criada = conquistaService.criar(request);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, criada.id()));
		return criada;
	}

	@PutMapping("/conquistas/{id}")
	public ConquistaResponse atualizarConquista(@PathVariable Long id, @Valid @RequestBody ConquistaRequest request) {
		return conquistaService.atualizar(id, request);
	}

	@DeleteMapping("/conquistas/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluirConquista(@PathVariable Long id) {
		conquistaService.excluir(id);
	}
}
