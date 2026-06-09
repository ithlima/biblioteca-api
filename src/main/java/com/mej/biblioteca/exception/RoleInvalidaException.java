package com.mej.biblioteca.exception;

@SuppressWarnings("java:S110")
public class RoleInvalidaException extends BusinessException {

    public RoleInvalidaException() {
        super("Role inválida. As roles permitidas são LEITOR e ADMIN.");
    }
}
