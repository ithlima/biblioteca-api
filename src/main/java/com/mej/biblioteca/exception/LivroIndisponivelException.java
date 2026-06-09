package com.mej.biblioteca.exception;

@SuppressWarnings("java:S110")
public class LivroIndisponivelException extends BusinessException {

    public LivroIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
