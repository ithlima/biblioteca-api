package com.mej.biblioteca.exception.domain;

import com.mej.biblioteca.exception.BusinessException;

public class EmprestimoOperacaoInvalidaException extends BusinessException {

    public EmprestimoOperacaoInvalidaException(String message) {
        super(message);
    }
}
