package com.mej.biblioteca.exception.domain;

import com.mej.biblioteca.exception.ApiException;
import org.springframework.http.HttpStatus;

@SuppressWarnings("java:S110")
public class AutenticacaoNecessariaException extends ApiException {

    public AutenticacaoNecessariaException() {
        super(HttpStatus.UNAUTHORIZED, "Não autorizado", "Autenticação necessária.");
    }
}
