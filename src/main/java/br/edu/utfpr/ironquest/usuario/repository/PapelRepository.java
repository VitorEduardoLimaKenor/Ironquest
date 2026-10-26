package br.edu.utfpr.ironquest.usuario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.utfpr.ironquest.usuario.domain.NomePapel;
import br.edu.utfpr.ironquest.usuario.domain.Papel;

public interface PapelRepository extends JpaRepository<Papel, Long> {

	Optional<Papel> findByNome(NomePapel nome);

	List<Papel> findAllByOrderByIdAsc();
}
