package com.mej.biblioteca.exception;

@SuppressWarnings("java:S110")
public class EmailEnvioException extends BusinessException {

    public EmailEnvioException(String mensagem) {
        super(mensagem);
    }
}
