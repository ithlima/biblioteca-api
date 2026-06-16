package com.mej.biblioteca.exception;

import org.springframework.http.HttpStatus;

public abstract class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String erro;

    protected ApiException(HttpStatus status, String erro, String mensagem) {
        super(mensagem);
        this.status = status;
        this.erro = erro;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErro() {
        return erro;
    }
}
