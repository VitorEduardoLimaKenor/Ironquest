package br.edu.utfpr.ironquest.personagem.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.utfpr.ironquest.personagem.domain.Personagem;
import br.edu.utfpr.ironquest.personagem.repository.PersonagemRepository;
import br.edu.utfpr.ironquest.personagem.web.dto.PersonagemRequest;
import br.edu.utfpr.ironquest.personagem.web.dto.PersonagemResponse;
import br.edu.utfpr.ironquest.shared.exception.ConflitoException;
import br.edu.utfpr.ironquest.shared.exception.RecursoNaoEncontradoException;
import br.edu.utfpr.ironquest.usuario.domain.Usuario;
import br.edu.utfpr.ironquest.usuario.service.UsuarioService;

@Service
@Transactional(readOnly = true)
public class PersonagemService {

	private final PersonagemRepository personagemRepository;
	private final UsuarioService usuarioService;

	public PersonagemService(PersonagemRepository personagemRepository, UsuarioService usuarioService) {
		this.personagemRepository = personagemRepository;
		this.usuarioService = usuarioService;
	}

	@Transactional
	public PersonagemResponse criar(Long usuarioId, PersonagemRequest request) {
		Usuario usuario = usuarioService.buscarAtivo(usuarioId);
		if (personagemRepository.existsByUsuarioId(usuarioId)) {
			throw new ConflitoException("personagem.ja-existe", usuarioId);
		}
		Personagem personagem = new Personagem(usuario, request.nome().trim(), request.objetivoPrincipal(),
				request.frequenciaSemanalAlvo());
		return PersonagemResponse.de(personagemRepository.save(personagem));
	}

	public PersonagemResponse buscar(Long id) {
		return PersonagemResponse.de(buscarEntidade(id));
	}

	public PersonagemResponse buscarPorUsuario(Long usuarioId) {
		return personagemRepository.findByUsuarioId(usuarioId)
				.map(PersonagemResponse::de)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Personagem do usuário", usuarioId));
	}

	@Transactional
	public PersonagemResponse atualizar(Long id, PersonagemRequest request) {
		Personagem personagem = buscarEntidade(id);
		personagem.definirPerfil(request.nome().trim(), request.objetivoPrincipal(), request.frequenciaSemanalAlvo());
		return PersonagemResponse.de(personagem);
	}

	/** Ponto de acesso de outros módulos ao personagem. */
	public Personagem buscarEntidade(Long id) {
		return personagemRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Personagem", id));
	}
}
