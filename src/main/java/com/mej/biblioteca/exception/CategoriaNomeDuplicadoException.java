package com.mej.biblioteca.exception;

@SuppressWarnings("java:S110")
public class CategoriaNomeDuplicadoException extends ConflictException {

    public CategoriaNomeDuplicadoException() {
        super("Já existe categoria cadastrada com esse nome.");
    }
}
