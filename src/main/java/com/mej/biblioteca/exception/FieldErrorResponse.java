package com.mej.biblioteca.exception;

public record FieldErrorResponse(
        String field,
        String message
) {
}
