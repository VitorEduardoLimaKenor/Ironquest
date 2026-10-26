package br.edu.utfpr.ironquest.shared.event;

import java.net.URI;

import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/** Adiciona o cabeçalho Location (URI absoluta do recurso criado) às respostas 201. */
@Component
public class RecursoCriadoListener implements ApplicationListener<RecursoCriadoEvent> {

	@Override
	public void onApplicationEvent(RecursoCriadoEvent evento) {
		UriComponentsBuilder base = evento.getCaminhoRecurso() == null
				? ServletUriComponentsBuilder.fromCurrentRequestUri()
				: ServletUriComponentsBuilder.fromCurrentContextPath().path(evento.getCaminhoRecurso());
		URI uri = base.path("/{id}").buildAndExpand(evento.getId()).toUri();
		evento.getResponse().setHeader(HttpHeaders.LOCATION, uri.toASCIIString());
	}
}
