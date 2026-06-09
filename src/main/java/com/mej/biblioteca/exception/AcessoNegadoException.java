package com.mej.biblioteca.exception;

import org.springframework.http.HttpStatus;

@SuppressWarnings("java:S110")
public class AcessoNegadoException extends ApiException {

    public AcessoNegadoException() {
        super(HttpStatus.FORBIDDEN, "Acesso negado", "Você não possui permissão para acessar este recurso.");
    }
}
