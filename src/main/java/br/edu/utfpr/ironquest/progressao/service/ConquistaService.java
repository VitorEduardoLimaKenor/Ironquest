package br.edu.utfpr.ironquest.progressao.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.utfpr.ironquest.progressao.domain.Conquista;
import br.edu.utfpr.ironquest.progressao.domain.Recompensa;
import br.edu.utfpr.ironquest.progressao.repository.ConquistaPersonagemRepository;
import br.edu.utfpr.ironquest.progressao.repository.ConquistaRepository;
import br.edu.utfpr.ironquest.progressao.web.dto.ProgressaoDtos.ConquistaRequest;
import br.edu.utfpr.ironquest.progressao.web.dto.ProgressaoDtos.ConquistaResponse;
import br.edu.utfpr.ironquest.shared.exception.ConflitoException;
import br.edu.utfpr.ironquest.shared.exception.RecursoNaoEncontradoException;

@Service
@Transactional(readOnly = true)
public class ConquistaService {

	private final ConquistaRepository conquistaRepository;
	private final ConquistaPersonagemRepository conquistaPersonagemRepository;
	private final RecompensaService recompensaService;

	public ConquistaService(ConquistaRepository conquistaRepository,
			ConquistaPersonagemRepository conquistaPersonagemRepository, RecompensaService recompensaService) {
		this.conquistaRepository = conquistaRepository;
		this.conquistaPersonagemRepository = conquistaPersonagemRepository;
		this.recompensaService = recompensaService;
	}

	public List<ConquistaResponse> listar() {
		return conquistaRepository.findAllByOrderByNomeAsc().stream().map(ConquistaResponse::de).toList();
	}

	public ConquistaResponse buscar(Long id) {
		return ConquistaResponse.de(buscarEntidade(id));
	}

	@Transactional
	public ConquistaResponse criar(ConquistaRequest request) {
		if (conquistaRepository.existsByCodigo(request.codigo())) {
			throw new ConflitoException("conquista.codigo-duplicado", request.codigo());
		}
		Conquista conquista = new Conquista(request.codigo(), request.nome().trim(), request.descricao(),
				request.regra(), resolverRecompensa(request.recompensaId()));
		return ConquistaResponse.de(conquistaRepository.save(conquista));
	}

	@Transactional
	public ConquistaResponse atualizar(Long id, ConquistaRequest request) {
		Conquista conquista = buscarEntidade(id);
		if (conquistaRepository.existsByCodigoAndIdNot(request.codigo(), id)) {
			throw new ConflitoException("conquista.codigo-duplicado", request.codigo());
		}
		conquista.atualizar(request.codigo(), request.nome().trim(), request.descricao(), request.regra(),
				resolverRecompensa(request.recompensaId()));
		return ConquistaResponse.de(conquista);
	}

	@Transactional
	public void excluir(Long id) {
		Conquista conquista = buscarEntidade(id);
		if (conquistaPersonagemRepository.existsByConquistaId(id)) {
			throw new ConflitoException("conquista.ja-concedida", id);
		}
		conquistaRepository.delete(conquista);
	}

	private Conquista buscarEntidade(Long id) {
		return conquistaRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Conquista", id));
	}

	private Recompensa resolverRecompensa(Long recompensaId) {
		return recompensaId == null ? null : recompensaService.buscarEntidade(recompensaId);
	}
}
