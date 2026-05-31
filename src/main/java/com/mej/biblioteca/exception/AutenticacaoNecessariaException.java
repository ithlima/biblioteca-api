package com.mej.biblioteca.exception;

import org.springframework.http.HttpStatus;

public class AutenticacaoNecessariaException extends ApiException {

    public AutenticacaoNecessariaException() {
        super(HttpStatus.UNAUTHORIZED, "Não autorizado", "Autenticação necessária.");
    }
}
