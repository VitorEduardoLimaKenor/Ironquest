package br.edu.utfpr.ironquest.treino.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.utfpr.ironquest.shared.exception.RecursoNaoEncontradoException;
import br.edu.utfpr.ironquest.shared.exception.RegraNegocioException;
import br.edu.utfpr.ironquest.treino.domain.PlanoTreino;
import br.edu.utfpr.ironquest.treino.repository.PlanoTreinoRepository;
import br.edu.utfpr.ironquest.treino.web.dto.PlanoTreinoDtos.ItemPlanoTreinoRequest;
import br.edu.utfpr.ironquest.treino.web.dto.PlanoTreinoDtos.PlanoTreinoRequest;
import br.edu.utfpr.ironquest.treino.web.dto.PlanoTreinoDtos.PlanoTreinoResponse;
import br.edu.utfpr.ironquest.usuario.domain.NomePapel;
import br.edu.utfpr.ironquest.usuario.domain.Usuario;
import br.edu.utfpr.ironquest.usuario.service.UsuarioService;

@Service
@Transactional(readOnly = true)
public class PlanoTreinoService {

	private final PlanoTreinoRepository planoTreinoRepository;
	private final UsuarioService usuarioService;
	private final ExercicioService exercicioService;

	public PlanoTreinoService(PlanoTreinoRepository planoTreinoRepository, UsuarioService usuarioService,
			ExercicioService exercicioService) {
		this.planoTreinoRepository = planoTreinoRepository;
		this.usuarioService = usuarioService;
		this.exercicioService = exercicioService;
	}

	public List<PlanoTreinoResponse> listarDoUsuario(Long usuarioId) {
		usuarioService.buscarEntidade(usuarioId);
		return planoTreinoRepository.findByUsuarioIdOrderByNomeAsc(usuarioId).stream()
				.map(PlanoTreinoResponse::de)
				.toList();
	}

	public PlanoTreinoResponse buscar(Long id) {
		return PlanoTreinoResponse.de(buscarEntidade(id));
	}

	@Transactional
	public PlanoTreinoResponse criar(Long usuarioId, PlanoTreinoRequest request) {
		Usuario usuario = usuarioService.buscarAtivo(usuarioId);
		PlanoTreino plano = new PlanoTreino(usuario, resolverAutor(usuario, request.autorId()),
				request.nome().trim(), request.descricao());
		adicionarItens(plano, request.itens());
		return PlanoTreinoResponse.de(planoTreinoRepository.save(plano));
	}

	@Transactional
	public PlanoTreinoResponse atualizar(Long id, PlanoTreinoRequest request) {
		PlanoTreino plano = buscarEntidade(id);
		boolean ativo = request.ativo() == null || request.ativo();
		plano.atualizar(resolverAutor(plano.getUsuario(), request.autorId()), request.nome().trim(),
				request.descricao(), ativo);
		plano.limparItens();
		// Remove os itens antigos antes de inserir os novos: evita colisão na restrição única (plano, ordem).
		planoTreinoRepository.flush();
		adicionarItens(plano, request.itens());
		planoTreinoRepository.flush();
		return PlanoTreinoResponse.de(plano);
	}

	/** Atualização parcial: altera somente a propriedade {@code ativo}. */
	@Transactional
	public void atualizarAtivo(Long id, boolean ativo) {
		buscarEntidade(id).definirAtivo(ativo);
	}

	@Transactional
	public void excluir(Long id) {
		planoTreinoRepository.delete(buscarEntidade(id));
	}

	PlanoTreino buscarEntidade(Long id) {
		return planoTreinoRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Plano de treino", id));
	}

	private void adicionarItens(PlanoTreino plano, List<ItemPlanoTreinoRequest> itens) {
		for (ItemPlanoTreinoRequest item : itens) {
			plano.adicionarItem(exercicioService.buscarAtivo(item.exercicioId()), item.seriesAlvo(),
					item.repeticoesAlvo(), item.descansoSegundos(), item.observacao());
		}
	}

	/** O autor é o próprio dono ou um treinador/administrador. */
	private Usuario resolverAutor(Usuario dono, Long autorId) {
		if (autorId == null || autorId.equals(dono.getId())) {
			return null;
		}
		Usuario autor = usuarioService.buscarAtivo(autorId);
		if (!autor.possuiPapel(NomePapel.TREINADOR) && !autor.possuiPapel(NomePapel.ADMINISTRADOR)) {
			throw new RegraNegocioException("usuario.autor-sem-permissao");
		}
		return autor;
	}
}
