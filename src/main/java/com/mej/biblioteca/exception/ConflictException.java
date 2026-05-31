package com.mej.biblioteca.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends ApiException {

    public ConflictException(String mensagem) {
        super(HttpStatus.CONFLICT, "Conflito", mensagem);
    }
}
