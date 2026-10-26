package br.edu.utfpr.ironquest.usuario.service;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.utfpr.ironquest.shared.exception.ConflitoException;
import br.edu.utfpr.ironquest.shared.exception.RecursoNaoEncontradoException;
import br.edu.utfpr.ironquest.usuario.domain.NomePapel;
import br.edu.utfpr.ironquest.usuario.domain.Papel;
import br.edu.utfpr.ironquest.usuario.domain.Usuario;
import br.edu.utfpr.ironquest.usuario.repository.PapelRepository;
import br.edu.utfpr.ironquest.usuario.repository.UsuarioRepository;
import br.edu.utfpr.ironquest.usuario.web.dto.AtualizarUsuarioRequest;
import br.edu.utfpr.ironquest.usuario.web.dto.CriarUsuarioRequest;
import br.edu.utfpr.ironquest.usuario.web.dto.PapelResponse;
import br.edu.utfpr.ironquest.usuario.web.dto.UsuarioResponse;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final PapelRepository papelRepository;
	private final PasswordEncoder passwordEncoder;

	public UsuarioService(UsuarioRepository usuarioRepository, PapelRepository papelRepository,
			PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.papelRepository = papelRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/** Lista usuários; {@code ativo} nulo traz todos. */
	public List<UsuarioResponse> listar(Boolean ativo) {
		List<Usuario> usuarios = ativo == null
				? usuarioRepository.findAllByOrderByNomeAsc()
				: usuarioRepository.findByAtivoOrderByNomeAsc(ativo);
		return usuarios.stream().map(UsuarioResponse::de).toList();
	}

	public UsuarioResponse buscar(Long id) {
		return UsuarioResponse.de(buscarEntidade(id));
	}

	public List<PapelResponse> listarPapeis() {
		return papelRepository.findAllByOrderByIdAsc().stream().map(PapelResponse::de).toList();
	}

	@Transactional
	public UsuarioResponse criar(CriarUsuarioRequest request) {
		String email = normalizarEmail(request.email());
		if (usuarioRepository.existsByEmail(email)) {
			throw new ConflitoException("usuario.email-duplicado", email);
		}
		Usuario usuario = new Usuario(request.nome().trim(), email, passwordEncoder.encode(request.senha()));
		Set<NomePapel> papeis = request.papeis() == null || request.papeis().isEmpty()
				? Set.of(NomePapel.JOGADOR)
				: request.papeis();
		papeis.forEach(nome -> usuario.adicionarPapel(buscarPapel(nome)));
		return UsuarioResponse.de(usuarioRepository.save(usuario));
	}

	@Transactional
	public UsuarioResponse atualizar(Long id, AtualizarUsuarioRequest request) {
		Usuario usuario = buscarEntidade(id);
		String email = normalizarEmail(request.email());
		if (usuarioRepository.existsByEmailAndIdNot(email, id)) {
			throw new ConflitoException("usuario.email-duplicado", email);
		}
		usuario.atualizar(request.nome().trim(), email);
		return UsuarioResponse.de(usuario);
	}

	/** Atualização parcial: altera somente a propriedade {@code ativo}. */
	@Transactional
	public void atualizarAtivo(Long id, boolean ativo) {
		buscarEntidade(id).definirAtivo(ativo);
	}

	/** Exclui a conta e, por cascata no banco, personagem, planos, sessões, recordes e progressão. */
	@Transactional
	public void excluir(Long id) {
		usuarioRepository.delete(buscarEntidade(id));
		usuarioRepository.flush();
	}

	/** Ponto de acesso de outros módulos ao usuário. */
	public Usuario buscarEntidade(Long id) {
		return usuarioRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
	}

	/** Usuário apto a registrar dados novos: existe e está ativo. */
	public Usuario buscarAtivo(Long id) {
		Usuario usuario = buscarEntidade(id);
		usuario.exigirAtivo();
		return usuario;
	}

	private Papel buscarPapel(NomePapel nome) {
		return papelRepository.findByNome(nome)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Papel", nome));
	}

	private static String normalizarEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}
}
