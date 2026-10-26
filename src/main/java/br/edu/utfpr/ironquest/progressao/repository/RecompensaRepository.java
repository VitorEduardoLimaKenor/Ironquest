package br.edu.utfpr.ironquest.progressao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.utfpr.ironquest.progressao.domain.Recompensa;

public interface RecompensaRepository extends JpaRepository<Recompensa, Long> {

	List<Recompensa> findAllByOrderByNomeAsc();
}
