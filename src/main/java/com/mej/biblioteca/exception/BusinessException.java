package com.mej.biblioteca.exception;

import org.springframework.http.HttpStatus;

@SuppressWarnings("java:S110")
public class BusinessException extends ApiException {

    public BusinessException(String message) {
        super(HttpStatus.BAD_REQUEST, "Regra de negócio violada", message);
    }
}
