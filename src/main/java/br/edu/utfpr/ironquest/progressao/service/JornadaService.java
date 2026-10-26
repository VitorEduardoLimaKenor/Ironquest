package br.edu.utfpr.ironquest.progressao.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.utfpr.ironquest.personagem.service.PersonagemService;
import br.edu.utfpr.ironquest.progressao.domain.Missao;
import br.edu.utfpr.ironquest.progressao.domain.StatusMissao;
import br.edu.utfpr.ironquest.progressao.repository.ConquistaPersonagemRepository;
import br.edu.utfpr.ironquest.progressao.repository.EventoProgressaoRepository;
import br.edu.utfpr.ironquest.progressao.repository.MissaoRepository;
import br.edu.utfpr.ironquest.progressao.web.dto.ProgressaoDtos.ConquistaObtidaResponse;
import br.edu.utfpr.ironquest.progressao.web.dto.ProgressaoDtos.EventoProgressaoResponse;
import br.edu.utfpr.ironquest.progressao.web.dto.ProgressaoDtos.MissaoResponse;

/**
 * Consultas da jornada do personagem: missões, conquistas obtidas e histórico de eventos.
 * A geração de missões e a concessão de recompensas pertencem ao motor de progressão (próximo checkpoint).
 */
@Service
@Transactional(readOnly = true)
public class JornadaService {

	private final PersonagemService personagemService;
	private final MissaoRepository missaoRepository;
	private final ConquistaPersonagemRepository conquistaPersonagemRepository;
	private final EventoProgressaoRepository eventoProgressaoRepository;

	public JornadaService(PersonagemService personagemService, MissaoRepository missaoRepository,
			ConquistaPersonagemRepository conquistaPersonagemRepository,
			EventoProgressaoRepository eventoProgressaoRepository) {
		this.personagemService = personagemService;
		this.missaoRepository = missaoRepository;
		this.conquistaPersonagemRepository = conquistaPersonagemRepository;
		this.eventoProgressaoRepository = eventoProgressaoRepository;
	}

	public List<MissaoResponse> listarMissoes(Long personagemId, StatusMissao status) {
		personagemService.buscarEntidade(personagemId);
		List<Missao> missoes = status == null
				? missaoRepository.findByPersonagemIdOrderByPrazoEmAsc(personagemId)
				: missaoRepository.findByPersonagemIdAndStatusOrderByPrazoEmAsc(personagemId, status);
		return missoes.stream().map(MissaoResponse::de).toList();
	}

	public List<ConquistaObtidaResponse> listarConquistas(Long personagemId) {
		personagemService.buscarEntidade(personagemId);
		return conquistaPersonagemRepository.findByPersonagemIdOrderByObtidaEmDesc(personagemId).stream()
				.map(ConquistaObtidaResponse::de)
				.toList();
	}

	public List<EventoProgressaoResponse> listarEventos(Long personagemId) {
		personagemService.buscarEntidade(personagemId);
		return eventoProgressaoRepository.findByPersonagemIdOrderByOcorridoEmDesc(personagemId).stream()
				.map(EventoProgressaoResponse::de)
				.toList();
	}
}
