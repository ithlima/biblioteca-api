package com.mej.biblioteca.exception;

import org.springframework.http.HttpStatus;

@SuppressWarnings("java:S110")
public class ConflictException extends ApiException {

    public ConflictException(String mensagem) {
        super(HttpStatus.CONFLICT, "Conflito", mensagem);
    }
}
