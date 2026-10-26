package br.edu.utfpr.ironquest.usuario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.utfpr.ironquest.usuario.domain.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	@EntityGraph(attributePaths = "papeis")
	List<Usuario> findAllByOrderByNomeAsc();

	@EntityGraph(attributePaths = "papeis")
	List<Usuario> findByAtivoOrderByNomeAsc(boolean ativo);

	@Override
	@EntityGraph(attributePaths = "papeis")
	Optional<Usuario> findById(Long id);

	boolean existsByEmail(String email);

	boolean existsByEmailAndIdNot(String email, Long id);
}
