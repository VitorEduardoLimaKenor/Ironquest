package br.edu.utfpr.ironquest.treino.service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.utfpr.ironquest.shared.exception.ConflitoException;
import br.edu.utfpr.ironquest.shared.exception.RecursoNaoEncontradoException;
import br.edu.utfpr.ironquest.shared.exception.RegraNegocioException;
import br.edu.utfpr.ironquest.treino.domain.PlanoTreino;
import br.edu.utfpr.ironquest.treino.domain.Serie;
import br.edu.utfpr.ironquest.treino.domain.SessaoTreino;
import br.edu.utfpr.ironquest.treino.domain.StatusSessao;
import br.edu.utfpr.ironquest.treino.repository.SessaoTreinoRepository;
import br.edu.utfpr.ironquest.treino.repository.filter.SessaoTreinoFilter;
import br.edu.utfpr.ironquest.treino.web.dto.SessaoTreinoDtos.FinalizacaoSessaoResponse;
import br.edu.utfpr.ironquest.treino.web.dto.SessaoTreinoDtos.FinalizarSessaoRequest;
import br.edu.utfpr.ironquest.treino.web.dto.SessaoTreinoDtos.IniciarSessaoRequest;
import br.edu.utfpr.ironquest.treino.web.dto.SessaoTreinoDtos.RecordePessoalResponse;
import br.edu.utfpr.ironquest.treino.web.dto.SessaoTreinoDtos.SerieRequest;
import br.edu.utfpr.ironquest.treino.web.dto.SessaoTreinoDtos.SerieResponse;
import br.edu.utfpr.ironquest.treino.web.dto.SessaoTreinoDtos.SessaoTreinoResponse;
import br.edu.utfpr.ironquest.treino.web.dto.SessaoTreinoDtos.SessaoTreinoResumoResponse;
import br.edu.utfpr.ironquest.usuario.domain.Usuario;
import br.edu.utfpr.ironquest.usuario.service.UsuarioService;

@Service
@Transactional(readOnly = true)
public class SessaoTreinoService {

	private final SessaoTreinoRepository sessaoTreinoRepository;
	private final UsuarioService usuarioService;
	private final PlanoTreinoService planoTreinoService;
	private final ExercicioService exercicioService;
	private final RecordePessoalService recordePessoalService;
	private final Clock relogio;

	public SessaoTreinoService(SessaoTreinoRepository sessaoTreinoRepository, UsuarioService usuarioService,
			PlanoTreinoService planoTreinoService, ExercicioService exercicioService,
			RecordePessoalService recordePessoalService, Clock relogio) {
		this.sessaoTreinoRepository = sessaoTreinoRepository;
		this.usuarioService = usuarioService;
		this.planoTreinoService = planoTreinoService;
		this.exercicioService = exercicioService;
		this.recordePessoalService = recordePessoalService;
		this.relogio = relogio;
	}

	/** Pesquisa paginada com filtros opcionais (Criteria API no repositório). */
	public Page<SessaoTreinoResumoResponse> pesquisar(SessaoTreinoFilter filtro, Pageable pageable) {
		return sessaoTreinoRepository.filtrar(filtro, pageable).map(SessaoTreinoResumoResponse::de);
	}

	public Page<SessaoTreinoResumoResponse> pesquisarDoUsuario(Long usuarioId, SessaoTreinoFilter filtro,
			Pageable pageable) {
		usuarioService.buscarEntidade(usuarioId);
		return pesquisar(filtro.doUsuario(usuarioId), pageable);
	}

	public SessaoTreinoResponse buscar(Long id) {
		return SessaoTreinoResponse.de(buscarEntidade(id));
	}

	@Transactional
	public SessaoTreinoResponse iniciar(Long usuarioId, IniciarSessaoRequest request) {
		Usuario usuario = usuarioService.buscarAtivo(usuarioId);
		if (sessaoTreinoRepository.existsByUsuarioIdAndStatus(usuarioId, StatusSessao.EM_ANDAMENTO)) {
			throw new ConflitoException("sessao.em-andamento-existente", usuarioId);
		}
		PlanoTreino plano = null;
		if (request.planoTreinoId() != null) {
			plano = planoTreinoService.buscarEntidade(request.planoTreinoId());
			if (!plano.getUsuario().getId().equals(usuarioId)) {
				throw new RegraNegocioException("plano.nao-pertence-ao-usuario", plano.getId(), usuarioId);
			}
		}
		Instant iniciadaEm = request.iniciadaEm() == null ? agora() : request.iniciadaEm();
		LocalDate dataTreino = LocalDate.ofInstant(iniciadaEm, relogio.getZone());
		SessaoTreino sessao = new SessaoTreino(usuario, plano, iniciadaEm, dataTreino, request.observacoes());
		return SessaoTreinoResponse.de(sessaoTreinoRepository.save(sessao));
	}

	/** Exclui sessão em andamento ou cancelada. Sessão finalizada faz parte do histórico. */
	@Transactional
	public void excluir(Long id) {
		SessaoTreino sessao = buscarEntidade(id);
		sessao.exigirExclusaoPermitida();
		sessaoTreinoRepository.delete(sessao);
	}

	public SerieResponse buscarSerie(Long sessaoId, Long serieId) {
		return SerieResponse.de(buscarEntidade(sessaoId).buscarSerie(serieId));
	}

	@Transactional
	public SerieResponse adicionarSerie(Long sessaoId, SerieRequest request) {
		SessaoTreino sessao = buscarEntidade(sessaoId);
		Serie serie = sessao.adicionarSerie(exercicioService.buscarAtivo(request.exercicioId()), request.tipo(),
				request.repeticoes(), request.cargaKg(), request.rpe(), request.rir());
		// Garante o id da nova série na resposta e no cabeçalho Location.
		sessaoTreinoRepository.flush();
		return SerieResponse.de(serie);
	}

	@Transactional
	public SerieResponse atualizarSerie(Long sessaoId, Long serieId, SerieRequest request) {
		SessaoTreino sessao = buscarEntidade(sessaoId);
		Serie serie = sessao.atualizarSerie(serieId, exercicioService.buscarAtivo(request.exercicioId()),
				request.tipo(), request.repeticoes(), request.cargaKg(), request.rpe(), request.rir());
		return SerieResponse.de(serie);
	}

	@Transactional
	public void removerSerie(Long sessaoId, Long serieId) {
		buscarEntidade(sessaoId).removerSerie(serieId);
	}

	/**
	 * Finaliza a sessão e atualiza os recordes pessoais. Idempotente: o reenvio de uma sessão já finalizada
	 * devolve o estado atual sem recalcular nada.
	 */
	@Transactional
	public FinalizacaoSessaoResponse finalizar(Long sessaoId, FinalizarSessaoRequest request) {
		SessaoTreino sessao = buscarEntidade(sessaoId);
		if (sessao.isFinalizada()) {
			return new FinalizacaoSessaoResponse(SessaoTreinoResponse.de(sessao), List.of(), true);
		}
		Instant finalizadaEm = request == null || request.finalizadaEm() == null ? agora() : request.finalizadaEm();
		sessao.finalizar(finalizadaEm);
		List<RecordePessoalResponse> novosRecordes = recordePessoalService.registrarResultados(sessao).stream()
				.map(RecordePessoalResponse::de)
				.toList();
		sessaoTreinoRepository.flush();
		return new FinalizacaoSessaoResponse(SessaoTreinoResponse.de(sessao), novosRecordes, false);
	}

	@Transactional
	public SessaoTreinoResponse cancelar(Long sessaoId) {
		SessaoTreino sessao = buscarEntidade(sessaoId);
		sessao.cancelar();
		return SessaoTreinoResponse.de(sessao);
	}

	private SessaoTreino buscarEntidade(Long id) {
		return sessaoTreinoRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Sessão de treino", id));
	}

	private Instant agora() {
		return Instant.now(relogio).truncatedTo(ChronoUnit.MICROS);
	}
}
