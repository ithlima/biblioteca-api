package com.mej.biblioteca.exception.domain;

import com.mej.biblioteca.exception.BusinessException;

public class RoleInvalidaException extends BusinessException {

    public RoleInvalidaException() {
        super("Role inválida. As roles permitidas são LEITOR e ADMIN.");
    }
}
