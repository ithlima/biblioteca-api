package com.mej.biblioteca.exception;

import org.springframework.http.HttpStatus;

@SuppressWarnings("java:S110")
public class AlteracaoRoleNaoPermitidaException extends ApiException {

    public AlteracaoRoleNaoPermitidaException(String mensagem) {
        super(HttpStatus.CONFLICT, "Conflito", mensagem);
    }
}
