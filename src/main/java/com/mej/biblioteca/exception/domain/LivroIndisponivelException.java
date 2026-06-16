package com.mej.biblioteca.exception.domain;

import com.mej.biblioteca.exception.BusinessException;

public class LivroIndisponivelException extends BusinessException {

    public LivroIndisponivelException(String message) {
        super(message);
    }
}
