package br.edu.utfpr.ironquest.progressao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.utfpr.ironquest.progressao.domain.EventoProgressao;

public interface EventoProgressaoRepository extends JpaRepository<EventoProgressao, Long> {

	List<EventoProgressao> findByPersonagemIdOrderByOcorridoEmDesc(Long personagemId);
}
