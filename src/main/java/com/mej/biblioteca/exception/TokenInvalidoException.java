package com.mej.biblioteca.exception;

import org.springframework.http.HttpStatus;

@SuppressWarnings("java:S110")
public class TokenInvalidoException extends ApiException {

    public TokenInvalidoException() {
        super(HttpStatus.UNAUTHORIZED, "Não autorizado", "Token inválido ou expirado.");
    }
}
