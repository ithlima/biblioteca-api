package com.mej.biblioteca.exception;

public class CategoriaNomeDuplicadoException extends ConflictException {

    public CategoriaNomeDuplicadoException() {
        super("Já existe categoria cadastrada com esse nome.");
    }
}
