package br.edu.utfpr.ironquest.progressao.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.utfpr.ironquest.progressao.domain.StatusMissao;
import br.edu.utfpr.ironquest.progressao.service.JornadaService;
import br.edu.utfpr.ironquest.progressao.web.dto.ProgressaoDtos.ConquistaObtidaResponse;
import br.edu.utfpr.ironquest.progressao.web.dto.ProgressaoDtos.EventoProgressaoResponse;
import br.edu.utfpr.ironquest.progressao.web.dto.ProgressaoDtos.MissaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/personagens/{personagemId}")
@Tag(name = "Jornada", description = "Missões, conquistas obtidas e histórico de progressão do personagem")
public class JornadaController {

	private final JornadaService jornadaService;

	public JornadaController(JornadaService jornadaService) {
		this.jornadaService = jornadaService;
	}

	@GetMapping("/missoes")
	@Operation(summary = "Missões do personagem, com filtro opcional por status")
	public List<MissaoResponse> listarMissoes(@PathVariable Long personagemId,
			@RequestParam(required = false) StatusMissao status) {
		return jornadaService.listarMissoes(personagemId, status);
	}

	@GetMapping("/conquistas")
	public List<ConquistaObtidaResponse> listarConquistas(@PathVariable Long personagemId) {
		return jornadaService.listarConquistas(personagemId);
	}

	@GetMapping("/eventos")
	@Operation(summary = "Histórico de eventos de progressão, mais recentes primeiro")
	public List<EventoProgressaoResponse> listarEventos(@PathVariable Long personagemId) {
		return jornadaService.listarEventos(personagemId);
	}
}
