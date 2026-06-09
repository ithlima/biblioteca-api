package com.mej.biblioteca.exception;

@SuppressWarnings("java:S110")
public class EmprestimoOperacaoInvalidaException extends BusinessException {

    public EmprestimoOperacaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
