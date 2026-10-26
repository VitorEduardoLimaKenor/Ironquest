package br.edu.utfpr.ironquest.treino.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.utfpr.ironquest.shared.exception.ConflitoException;
import br.edu.utfpr.ironquest.shared.exception.RecursoNaoEncontradoException;
import br.edu.utfpr.ironquest.treino.domain.GrupoMuscular;
import br.edu.utfpr.ironquest.treino.repository.ExercicioRepository;
import br.edu.utfpr.ironquest.treino.repository.GrupoMuscularRepository;
import br.edu.utfpr.ironquest.treino.web.dto.GrupoMuscularDtos.GrupoMuscularRequest;
import br.edu.utfpr.ironquest.treino.web.dto.GrupoMuscularDtos.GrupoMuscularResponse;

@Service
@Transactional(readOnly = true)
public class GrupoMuscularService {

	private final GrupoMuscularRepository grupoMuscularRepository;
	private final ExercicioRepository exercicioRepository;

	public GrupoMuscularService(GrupoMuscularRepository grupoMuscularRepository,
			ExercicioRepository exercicioRepository) {
		this.grupoMuscularRepository = grupoMuscularRepository;
		this.exercicioRepository = exercicioRepository;
	}

	public List<GrupoMuscularResponse> listar() {
		return grupoMuscularRepository.findAllByOrderByNomeAsc().stream().map(GrupoMuscularResponse::de).toList();
	}

	public GrupoMuscularResponse buscar(Long id) {
		return GrupoMuscularResponse.de(buscarEntidade(id));
	}

	@Transactional
	public GrupoMuscularResponse criar(GrupoMuscularRequest request) {
		String nome = request.nome().trim();
		if (grupoMuscularRepository.existsByNome(nome)) {
			throw new ConflitoException("grupo-muscular.nome-duplicado", nome);
		}
		return GrupoMuscularResponse.de(grupoMuscularRepository.save(new GrupoMuscular(nome, request.descricao())));
	}

	@Transactional
	public GrupoMuscularResponse atualizar(Long id, GrupoMuscularRequest request) {
		GrupoMuscular grupo = buscarEntidade(id);
		String nome = request.nome().trim();
		if (grupoMuscularRepository.existsByNomeAndIdNot(nome, id)) {
			throw new ConflitoException("grupo-muscular.nome-duplicado", nome);
		}
		grupo.atualizar(nome, request.descricao());
		return GrupoMuscularResponse.de(grupo);
	}

	/** Verifica o vínculo antes de excluir, para devolver uma mensagem específica. */
	@Transactional
	public void excluir(Long id) {
		GrupoMuscular grupo = buscarEntidade(id);
		if (exercicioRepository.existsByGrupoMuscularId(id)) {
			throw new ConflitoException("grupo-muscular.possui-exercicios", id);
		}
		grupoMuscularRepository.delete(grupo);
	}

	GrupoMuscular buscarEntidade(Long id) {
		return grupoMuscularRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Grupo muscular", id));
	}
}
