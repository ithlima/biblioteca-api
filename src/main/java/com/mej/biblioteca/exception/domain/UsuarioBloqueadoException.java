package com.mej.biblioteca.exception.domain;

import com.mej.biblioteca.exception.ApiException;
import org.springframework.http.HttpStatus;

@SuppressWarnings("java:S110")
public class UsuarioBloqueadoException extends ApiException {

    public UsuarioBloqueadoException() {
        super(HttpStatus.UNAUTHORIZED, "Não autorizado", "Usuário bloqueado.");
    }
}
