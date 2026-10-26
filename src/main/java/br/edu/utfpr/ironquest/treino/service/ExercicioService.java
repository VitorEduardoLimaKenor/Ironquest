package br.edu.utfpr.ironquest.treino.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.utfpr.ironquest.shared.exception.ConflitoException;
import br.edu.utfpr.ironquest.shared.exception.RecursoNaoEncontradoException;
import br.edu.utfpr.ironquest.shared.exception.RegraNegocioException;
import br.edu.utfpr.ironquest.treino.domain.Exercicio;
import br.edu.utfpr.ironquest.treino.domain.GrupoMuscular;
import br.edu.utfpr.ironquest.treino.repository.ExercicioRepository;
import br.edu.utfpr.ironquest.treino.web.dto.ExercicioDtos.ExercicioRequest;
import br.edu.utfpr.ironquest.treino.web.dto.ExercicioDtos.ExercicioResponse;

@Service
@Transactional(readOnly = true)
public class ExercicioService {

	private final ExercicioRepository exercicioRepository;
	private final GrupoMuscularService grupoMuscularService;

	public ExercicioService(ExercicioRepository exercicioRepository, GrupoMuscularService grupoMuscularService) {
		this.exercicioRepository = exercicioRepository;
		this.grupoMuscularService = grupoMuscularService;
	}

	/** Lista exercícios; filtros nulos não restringem (ex.: {@code ativo=true} para o catálogo). */
	public List<ExercicioResponse> listar(Long grupoMuscularId, Boolean ativo) {
		return exercicioRepository.pesquisar(grupoMuscularId, ativo).stream().map(ExercicioResponse::de).toList();
	}

	public ExercicioResponse buscar(Long id) {
		return ExercicioResponse.de(buscarEntidade(id));
	}

	@Transactional
	public ExercicioResponse criar(ExercicioRequest request) {
		String nome = request.nome().trim();
		if (exercicioRepository.existsByNome(nome)) {
			throw new ConflitoException("exercicio.nome-duplicado", nome);
		}
		GrupoMuscular grupo = grupoMuscularService.buscarEntidade(request.grupoMuscularId());
		Exercicio exercicio = new Exercicio(nome, request.descricao(), grupo, request.tipo(), request.equipamento());
		return ExercicioResponse.de(exercicioRepository.save(exercicio));
	}

	@Transactional
	public ExercicioResponse atualizar(Long id, ExercicioRequest request) {
		Exercicio exercicio = buscarEntidade(id);
		String nome = request.nome().trim();
		if (exercicioRepository.existsByNomeAndIdNot(nome, id)) {
			throw new ConflitoException("exercicio.nome-duplicado", nome);
		}
		GrupoMuscular grupo = grupoMuscularService.buscarEntidade(request.grupoMuscularId());
		exercicio.atualizar(nome, request.descricao(), grupo, request.tipo(), request.equipamento());
		return ExercicioResponse.de(exercicio);
	}

	/** Atualização parcial: altera somente a propriedade {@code ativo}. */
	@Transactional
	public void atualizarAtivo(Long id, boolean ativo) {
		buscarEntidade(id).definirAtivo(ativo);
	}

	/**
	 * Exclusão física. Se o exercício já aparece em planos, séries ou recordes, a chave estrangeira do banco
	 * barra a operação e a API responde "operação não permitida"; nesse caso, desative-o.
	 */
	@Transactional
	public void excluir(Long id) {
		exercicioRepository.delete(buscarEntidade(id));
		exercicioRepository.flush();
	}

	Exercicio buscarEntidade(Long id) {
		return exercicioRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Exercício", id));
	}

	/** Exercício disponível para novos planos e séries. */
	Exercicio buscarAtivo(Long id) {
		Exercicio exercicio = buscarEntidade(id);
		if (!exercicio.isAtivo()) {
			throw new RegraNegocioException("exercicio.inativo", id);
		}
		return exercicio;
	}
}
