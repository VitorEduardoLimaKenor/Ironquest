package br.edu.utfpr.ironquest.progressao.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.utfpr.ironquest.progressao.domain.Recompensa;
import br.edu.utfpr.ironquest.progressao.repository.RecompensaRepository;
import br.edu.utfpr.ironquest.progressao.web.dto.ProgressaoDtos.RecompensaRequest;
import br.edu.utfpr.ironquest.progressao.web.dto.ProgressaoDtos.RecompensaResponse;
import br.edu.utfpr.ironquest.shared.exception.RecursoNaoEncontradoException;
import br.edu.utfpr.ironquest.shared.exception.RegraNegocioException;

@Service
@Transactional(readOnly = true)
public class RecompensaService {

	private final RecompensaRepository recompensaRepository;

	public RecompensaService(RecompensaRepository recompensaRepository) {
		this.recompensaRepository = recompensaRepository;
	}

	public List<RecompensaResponse> listar() {
		return recompensaRepository.findAllByOrderByNomeAsc().stream().map(RecompensaResponse::de).toList();
	}

	public RecompensaResponse buscar(Long id) {
		return RecompensaResponse.de(buscarEntidade(id));
	}

	@Transactional
	public RecompensaResponse criar(RecompensaRequest request) {
		int pontos = validarAtributo(request);
		Recompensa recompensa = new Recompensa(request.nome().trim(), request.descricao(), request.experiencia(),
				request.tipoAtributo(), pontos);
		return RecompensaResponse.de(recompensaRepository.save(recompensa));
	}

	@Transactional
	public RecompensaResponse atualizar(Long id, RecompensaRequest request) {
		Recompensa recompensa = buscarEntidade(id);
		int pontos = validarAtributo(request);
		recompensa.atualizar(request.nome().trim(), request.descricao(), request.experiencia(),
				request.tipoAtributo(), pontos);
		return RecompensaResponse.de(recompensa);
	}

	/** Conquistas e missões vinculadas ficam sem recompensa (ON DELETE SET NULL). */
	@Transactional
	public void excluir(Long id) {
		recompensaRepository.delete(buscarEntidade(id));
	}

	Recompensa buscarEntidade(Long id) {
		return recompensaRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Recompensa", id));
	}

	private static int validarAtributo(RecompensaRequest request) {
		int pontos = request.pontosAtributo() == null ? 0 : request.pontosAtributo();
		if (pontos > 0 && request.tipoAtributo() == null) {
			throw new RegraNegocioException("recompensa.atributo-obrigatorio");
		}
		return pontos;
	}
}
