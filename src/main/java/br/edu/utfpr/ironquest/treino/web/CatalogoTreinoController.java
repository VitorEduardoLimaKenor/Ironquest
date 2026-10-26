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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.edu.utfpr.ironquest.shared.event.RecursoCriadoEvent;
import br.edu.utfpr.ironquest.treino.service.ExercicioService;
import br.edu.utfpr.ironquest.treino.service.GrupoMuscularService;
import br.edu.utfpr.ironquest.treino.web.dto.ExercicioDtos.ExercicioRequest;
import br.edu.utfpr.ironquest.treino.web.dto.ExercicioDtos.ExercicioResponse;
import br.edu.utfpr.ironquest.treino.web.dto.GrupoMuscularDtos.GrupoMuscularRequest;
import br.edu.utfpr.ironquest.treino.web.dto.GrupoMuscularDtos.GrupoMuscularResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Tag(name = "Catálogo de treino", description = "Grupos musculares e exercícios")
public class CatalogoTreinoController {

	private final GrupoMuscularService grupoMuscularService;
	private final ExercicioService exercicioService;
	private final ApplicationEventPublisher publisher;

	public CatalogoTreinoController(GrupoMuscularService grupoMuscularService, ExercicioService exercicioService,
			ApplicationEventPublisher publisher) {
		this.grupoMuscularService = grupoMuscularService;
		this.exercicioService = exercicioService;
		this.publisher = publisher;
	}

	@GetMapping("/grupos-musculares")
	public List<GrupoMuscularResponse> listarGrupos() {
		return grupoMuscularService.listar();
	}

	@GetMapping("/grupos-musculares/{id}")
	public GrupoMuscularResponse buscarGrupo(@PathVariable Long id) {
		return grupoMuscularService.buscar(id);
	}

	@PostMapping("/grupos-musculares")
	@ResponseStatus(HttpStatus.CREATED)
	public GrupoMuscularResponse criarGrupo(@Valid @RequestBody GrupoMuscularRequest request,
			HttpServletResponse response) {
		GrupoMuscularResponse criado = grupoMuscularService.criar(request);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, criado.id()));
		return criado;
	}

	@PutMapping("/grupos-musculares/{id}")
	public GrupoMuscularResponse atualizarGrupo(@PathVariable Long id,
			@Valid @RequestBody GrupoMuscularRequest request) {
		return grupoMuscularService.atualizar(id, request);
	}

	@DeleteMapping("/grupos-musculares/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Exclui grupo muscular sem exercícios vinculados")
	public void excluirGrupo(@PathVariable Long id) {
		grupoMuscularService.excluir(id);
	}

	@GetMapping("/exercicios")
	@Operation(summary = "Lista exercícios; filtros opcionais por grupo muscular e ativo")
	public List<ExercicioResponse> listarExercicios(@RequestParam(required = false) Long grupoMuscularId,
			@RequestParam(required = false) Boolean ativo) {
		return exercicioService.listar(grupoMuscularId, ativo);
	}

	@GetMapping("/exercicios/{id}")
	public ExercicioResponse buscarExercicio(@PathVariable Long id) {
		return exercicioService.buscar(id);
	}

	@PostMapping("/exercicios")
	@ResponseStatus(HttpStatus.CREATED)
	public ExercicioResponse criarExercicio(@Valid @RequestBody ExercicioRequest request,
			HttpServletResponse response) {
		ExercicioResponse criado = exercicioService.criar(request);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, criado.id()));
		return criado;
	}

	@PutMapping("/exercicios/{id}")
	public ExercicioResponse atualizarExercicio(@PathVariable Long id, @Valid @RequestBody ExercicioRequest request) {
		return exercicioService.atualizar(id, request);
	}

	@PutMapping("/exercicios/{id}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Ativa ou desativa o exercício (atualização parcial; corpo: true ou false)")
	public void atualizarAtivoExercicio(@PathVariable Long id, @RequestBody Boolean ativo) {
		exercicioService.atualizarAtivo(id, ativo);
	}

	@DeleteMapping("/exercicios/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Exclui exercício sem histórico; com histórico, responde 409 (desative-o)")
	public void excluirExercicio(@PathVariable Long id) {
		exercicioService.excluir(id);
	}
}
