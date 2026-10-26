package br.edu.utfpr.ironquest.personagem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.utfpr.ironquest.personagem.domain.Personagem;

public interface PersonagemRepository extends JpaRepository<Personagem, Long> {

	@Override
	@EntityGraph(attributePaths = { "usuario", "atributos" })
	Optional<Personagem> findById(Long id);

	@EntityGraph(attributePaths = { "usuario", "atributos" })
	Optional<Personagem> findByUsuarioId(Long usuarioId);

	boolean existsByUsuarioId(Long usuarioId);
}
