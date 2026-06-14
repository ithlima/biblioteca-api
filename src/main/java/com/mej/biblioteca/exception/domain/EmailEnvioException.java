package com.mej.biblioteca.exception.domain;

import com.mej.biblioteca.exception.BusinessException;

public class EmailEnvioException extends BusinessException {

    public EmailEnvioException(String message) {
        super(message);
    }
}
