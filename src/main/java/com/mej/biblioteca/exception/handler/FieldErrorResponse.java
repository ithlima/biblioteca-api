package com.mej.biblioteca.exception.handler;

public record FieldErrorResponse(
        String campo,
        String mensagem
) {
}
