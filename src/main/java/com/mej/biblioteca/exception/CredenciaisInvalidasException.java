package com.mej.biblioteca.exception;

import org.springframework.http.HttpStatus;

public class CredenciaisInvalidasException extends ApiException {

    public CredenciaisInvalidasException() {
        super(HttpStatus.UNAUTHORIZED, "Não autorizado", "Credenciais inválidas.");
    }
}
