package com.mej.biblioteca.exception.domain;

import com.mej.biblioteca.exception.ApiException;
import org.springframework.http.HttpStatus;

@SuppressWarnings("java:S110")
public class UltimoAdministradorException extends ApiException {

    public UltimoAdministradorException() {
        super(HttpStatus.CONFLICT, "Conflito", "Não é permitido remover os privilégios do último administrador do sistema.");
    }
}
