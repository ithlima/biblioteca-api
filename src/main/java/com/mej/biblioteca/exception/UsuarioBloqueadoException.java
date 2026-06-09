package com.mej.biblioteca.exception;

import org.springframework.http.HttpStatus;

@SuppressWarnings("java:S110")
public class UsuarioBloqueadoException extends ApiException {

    public UsuarioBloqueadoException() {
        super(HttpStatus.UNAUTHORIZED, "Não autorizado", "Usuário bloqueado.");
    }
}
