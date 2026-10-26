package br.edu.utfpr.ironquest.shared.web;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.edu.utfpr.ironquest.shared.exception.ConflitoException;
import br.edu.utfpr.ironquest.shared.exception.IronQuestException;
import br.edu.utfpr.ironquest.shared.exception.ParametroInvalidoException;
import br.edu.utfpr.ironquest.shared.exception.RecursoNaoEncontradoException;
import br.edu.utfpr.ironquest.shared.exception.RegraNegocioException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

/**
 * Converte exceções em respostas RFC 9457 (application/problem+json) com a lista {@code erros}. Cada erro traz a
 * mensagem para o usuário (de {@code messages.properties}) e, opcionalmente, a causa técnica para o desenvolvedor.
 */
@RestControllerAdvice
public class TratadorGlobalExcecoes extends ResponseEntityExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(TratadorGlobalExcecoes.class);

	private final MessageSource mensagens;
	private final boolean exporDetalhes;

	public TratadorGlobalExcecoes(MessageSource mensagens,
			@Value("${ironquest.erros.expor-detalhes:true}") boolean exporDetalhes) {
		this.mensagens = mensagens;
		this.exporDetalhes = exporDetalhes;
	}

	/** Corpo ausente, JSON malformado, atributo desconhecido ou valor incompatível com o tipo do atributo. */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		Throwable causa = NestedExceptionUtils.getMostSpecificCause(ex);
		String mensagemUsuario = switch (causa) {
			case UnrecognizedPropertyException e -> mensagem("mensagem.atributo-desconhecido", e.getPropertyName());
			case MismatchedInputException e when !e.getPath().isEmpty() ->
				mensagem("mensagem.valor-invalido", caminhoJson(e));
			// Sem causa: o Spring não recebeu corpo (ou recebeu "null").
			case HttpMessageNotReadableException e -> mensagem("mensagem.corpo-ausente");
			default -> mensagem("mensagem.invalida");
		};
		return responder(HttpStatus.BAD_REQUEST, "erro.titulo.mensagem-invalida", mensagemUsuario,
				List.of(erro(null, mensagemUsuario, causa)));
	}

	/** Falhas de Bean Validation em {@code @Valid @RequestBody} e em filtros de pesquisa. */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		return responder(HttpStatus.BAD_REQUEST, "erro.titulo.dados-invalidos", mensagem("dados.invalidos"),
				errosDe(ex.getBindingResult()));
	}

	/** Parâmetro de URL ou de consulta com tipo errado (ex.: /api/usuarios/abc). */
	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
			HttpStatusCode status, WebRequest request) {
		String parametro = ex instanceof MethodArgumentTypeMismatchException argumento
				? argumento.getName()
				: ex.getPropertyName();
		String mensagemUsuario = mensagem("parametro.tipo-invalido", parametro, String.valueOf(ex.getValue()));
		return responder(HttpStatus.BAD_REQUEST, "erro.titulo.parametro-invalido", mensagemUsuario,
				List.of(erro(parametro, mensagemUsuario, ex)));
	}

	/** Demais erros tratados pelo Spring MVC (404 de rota, 405, 415...): acrescenta a lista {@code erros}. */
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatusCode statusCode, WebRequest request) {
		ResponseEntity<Object> resposta = super.handleExceptionInternal(ex, body, headers, statusCode, request);
		if (resposta != null && resposta.getBody() instanceof ProblemDetail problema
				&& (problema.getProperties() == null || !problema.getProperties().containsKey("erros"))) {
			problema.setProperty("erros", List.of(erro(null, problema.getDetail(), ex)));
		}
		return resposta;
	}

	@ExceptionHandler(RecursoNaoEncontradoException.class)
	public ResponseEntity<Object> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
		return responder(HttpStatus.NOT_FOUND, "erro.titulo.recurso-nao-encontrado", ex);
	}

	@ExceptionHandler(ConflitoException.class)
	public ResponseEntity<Object> tratarConflito(ConflitoException ex) {
		return responder(HttpStatus.CONFLICT, "erro.titulo.conflito", ex);
	}

	/** Regras de negócio, incluindo {@code UsuarioInativoException}. */
	@ExceptionHandler(RegraNegocioException.class)
	public ResponseEntity<Object> tratarRegraNegocio(RegraNegocioException ex) {
		return responder(HttpStatus.UNPROCESSABLE_CONTENT, "erro.titulo.regra-negocio", ex);
	}

	@ExceptionHandler(ParametroInvalidoException.class)
	public ResponseEntity<Object> tratarParametroInvalido(ParametroInvalidoException ex) {
		return responder(HttpStatus.BAD_REQUEST, "erro.titulo.parametro-invalido", ex);
	}

	/** Restrição do banco violada (chave estrangeira, unicidade, CHECK): "operação não permitida". */
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Object> tratarIntegridade(DataIntegrityViolationException ex) {
		String mensagemUsuario = mensagem("recurso.operacao-nao-permitida");
		return responder(HttpStatus.CONFLICT, "erro.titulo.operacao-nao-permitida", mensagemUsuario,
				List.of(erro(null, mensagemUsuario, NestedExceptionUtils.getMostSpecificCause(ex))));
	}

	@ExceptionHandler(ObjectOptimisticLockingFailureException.class)
	public ResponseEntity<Object> tratarConcorrencia(ObjectOptimisticLockingFailureException ex) {
		String mensagemUsuario = mensagem("recurso.atualizacao-concorrente");
		return responder(HttpStatus.CONFLICT, "erro.titulo.atualizacao-concorrente", mensagemUsuario,
				List.of(erro(null, mensagemUsuario, ex)));
	}

	/** Último recurso: registra o erro e não expõe a pilha ao cliente. */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> tratarErroInesperado(Exception ex) {
		LOG.error("Erro inesperado ao processar a requisição", ex);
		String mensagemUsuario = mensagem("erro.interno");
		return responder(HttpStatus.INTERNAL_SERVER_ERROR, "erro.titulo.erro-interno", mensagemUsuario,
				List.of(erro(null, mensagemUsuario, ex)));
	}

	private ResponseEntity<Object> responder(HttpStatus status, String codigoTitulo, IronQuestException ex) {
		String mensagemUsuario = mensagens.getMessage(ex.getCodigoMensagem(), ex.getArgumentos(),
				ex.getCodigoMensagem(), LocaleContextHolder.getLocale());
		return responder(status, codigoTitulo, mensagemUsuario, List.of(erro(null, mensagemUsuario, ex)));
	}

	private ResponseEntity<Object> responder(HttpStatus status, String codigoTitulo, String detalhe,
			List<Erro> erros) {
		ProblemDetail problema = ProblemDetail.forStatusAndDetail(status, detalhe);
		problema.setTitle(mensagem(codigoTitulo));
		problema.setProperty("erros", erros);
		return ResponseEntity.status(status).body(problema);
	}

	private List<Erro> errosDe(BindingResult resultado) {
		List<Erro> erros = new ArrayList<>();
		resultado.getFieldErrors().forEach(erroCampo -> erros.add(erroDeCampo(erroCampo)));
		for (ObjectError erroGlobal : resultado.getGlobalErrors()) {
			erros.add(new Erro(null, mensagem(erroGlobal), detalhe(erroGlobal.toString())));
		}
		erros.sort(Comparator.comparing(Erro::campo, Comparator.nullsLast(Comparator.naturalOrder())));
		return erros;
	}

	private Erro erroDeCampo(FieldError erroCampo) {
		return new Erro(erroCampo.getField(), mensagem(erroCampo), detalhe(erroCampo.toString()));
	}

	private Erro erro(String campo, String mensagemUsuario, Throwable causa) {
		return new Erro(campo, mensagemUsuario, detalhe(causa.toString()));
	}

	private String detalhe(String mensagemDesenvolvedor) {
		return exporDetalhes ? mensagemDesenvolvedor : null;
	}

	private String mensagem(String codigo, Object... argumentos) {
		return mensagens.getMessage(codigo, argumentos, LocaleContextHolder.getLocale());
	}

	private String mensagem(MessageSourceResolvable resolvable) {
		return mensagens.getMessage(resolvable, LocaleContextHolder.getLocale());
	}

	/** Caminho do atributo no JSON, ex.: "itens[1].seriesAlvo". */
	private static String caminhoJson(MismatchedInputException ex) {
		return ex.getPath().stream()
				.map(TratadorGlobalExcecoes::trecho)
				.collect(Collectors.joining("."))
				.replace(".[", "[");
	}

	private static String trecho(JacksonException.Reference referencia) {
		return referencia.getPropertyName() != null
				? referencia.getPropertyName()
				: "[" + referencia.getIndex() + "]";
	}
}
