package com.mej.biblioteca.exception;

import org.springframework.http.HttpStatus;

@SuppressWarnings("java:S110")
public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "Recurso não encontrado", message);
    }
}
