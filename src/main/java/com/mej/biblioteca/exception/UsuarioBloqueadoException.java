package com.mej.biblioteca.exception;

import org.springframework.http.HttpStatus;

public class UsuarioBloqueadoException extends ApiException {

    public UsuarioBloqueadoException() {
        super(HttpStatus.UNAUTHORIZED, "Não autorizado", "Usuário bloqueado.");
    }
}
