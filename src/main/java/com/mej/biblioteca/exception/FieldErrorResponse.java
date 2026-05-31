package com.mej.biblioteca.exception;

public record FieldErrorResponse(
        String campo,
        String mensagem
) {
}
