package br.edu.utfpr.ironquest.treino.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.edu.utfpr.ironquest.treino.domain.RecordePessoal;
import br.edu.utfpr.ironquest.treino.domain.Serie;
import br.edu.utfpr.ironquest.treino.domain.SessaoTreino;
import br.edu.utfpr.ironquest.treino.domain.TipoRecorde;
import br.edu.utfpr.ironquest.treino.repository.RecordePessoalRepository;
import br.edu.utfpr.ironquest.treino.web.dto.SessaoTreinoDtos.RecordePessoalResponse;
import br.edu.utfpr.ironquest.usuario.service.UsuarioService;

@Service
@Transactional(readOnly = true)
public class RecordePessoalService {

	private final RecordePessoalRepository recordePessoalRepository;
	private final UsuarioService usuarioService;

	public RecordePessoalService(RecordePessoalRepository recordePessoalRepository, UsuarioService usuarioService) {
		this.recordePessoalRepository = recordePessoalRepository;
		this.usuarioService = usuarioService;
	}

	public List<RecordePessoalResponse> listar(Long usuarioId, Long exercicioId) {
		usuarioService.buscarEntidade(usuarioId);
		List<RecordePessoal> recordes = exercicioId == null
				? recordePessoalRepository.findByUsuarioIdOrderByExercicioNomeAscTipoAsc(usuarioId)
				: recordePessoalRepository.findByUsuarioIdAndExercicioIdOrderByTipoAsc(usuarioId, exercicioId);
		return recordes.stream().map(RecordePessoalResponse::de).toList();
	}

	/**
	 * Compara as séries de trabalho de uma sessão finalizada com os recordes atuais do usuário.
	 * Retorna os recordes criados ou superados, sem repetição.
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	public List<RecordePessoal> registrarResultados(SessaoTreino sessao) {
		List<Serie> seriesTrabalho = sessao.getSeries().stream().filter(Serie::isTrabalho).toList();
		Set<Long> exercicioIds = seriesTrabalho.stream()
				.map(serie -> serie.getExercicio().getId())
				.collect(Collectors.toSet());

		Map<ChaveRecorde, RecordePessoal> atuais = new HashMap<>();
		for (RecordePessoal recorde : recordePessoalRepository
				.findByUsuarioIdAndExercicioIdIn(sessao.getUsuario().getId(), exercicioIds)) {
			atuais.put(new ChaveRecorde(recorde.getExercicio().getId(), recorde.getTipo()), recorde);
		}

		Map<ChaveRecorde, RecordePessoal> novos = new LinkedHashMap<>();
		for (Serie serie : seriesTrabalho) {
			for (TipoRecorde tipo : TipoRecorde.values()) {
				BigDecimal valor = valorDe(serie, tipo);
				if (valor.signum() <= 0) {
					continue;
				}
				ChaveRecorde chave = new ChaveRecorde(serie.getExercicio().getId(), tipo);
				RecordePessoal atual = atuais.get(chave);
				if (atual == null) {
					RecordePessoal criado = recordePessoalRepository.save(new RecordePessoal(sessao.getUsuario(),
							serie.getExercicio(), tipo, valor, serie, sessao.getFinalizadaEm()));
					atuais.put(chave, criado);
					novos.put(chave, criado);
				} else if (atual.superar(valor, serie, sessao.getFinalizadaEm())) {
					novos.put(chave, atual);
				}
			}
		}
		return List.copyOf(novos.values());
	}

	private static BigDecimal valorDe(Serie serie, TipoRecorde tipo) {
		return switch (tipo) {
			case MAIOR_CARGA -> serie.getCargaKg();
			case MAIOR_1RM_ESTIMADO -> serie.getUmRmEstimadoKg();
			case MAIOR_VOLUME_SERIE -> serie.getVolumeKg();
		};
	}

	private record ChaveRecorde(Long exercicioId, TipoRecorde tipo) {
	}
}
