package com.mej.biblioteca.exception;

public class RoleInvalidaException extends BusinessException {

    public RoleInvalidaException() {
        super("Role inválida. As roles permitidas são LEITOR e ADMIN.");
    }
}
