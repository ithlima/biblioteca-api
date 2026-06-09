package com.mej.biblioteca.exception;

@SuppressWarnings("java:S110")
public class CodigoVerificacaoInvalidoException extends BusinessException {

    public CodigoVerificacaoInvalidoException() {
        super("Código de verificação inválido ou expirado.");
    }
}
