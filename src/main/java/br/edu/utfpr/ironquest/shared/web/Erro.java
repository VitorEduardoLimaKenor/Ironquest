package br.edu.utfpr.ironquest.shared.web;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Item da lista {@code erros} das respostas de erro.
 *
 * @param campo campo da requisição com problema (só em erros de validação)
 * @param mensagemUsuario texto amigável, vindo de {@code messages.properties}
 * @param mensagemDesenvolvedor causa técnica; omitida quando {@code ironquest.erros.expor-detalhes=false}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Erro(String campo, String mensagemUsuario, String mensagemDesenvolvedor) {
}
