package com.mej.biblioteca.exception.domain;

import com.mej.biblioteca.exception.BusinessException;

public class CodigoVerificacaoInvalidoException extends BusinessException {

    public CodigoVerificacaoInvalidoException() {
        super("Código de verificação inválido ou expirado.");
    }
}
