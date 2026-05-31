package com.mej.biblioteca.exception;

public class CodigoVerificacaoInvalidoException extends BusinessException {

    public CodigoVerificacaoInvalidoException() {
        super("Código de verificação inválido ou expirado.");
    }
}
