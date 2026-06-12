package com.mej.biblioteca.exception.domain;

import com.mej.biblioteca.exception.ConflictException;

public class CategoriaNomeDuplicadoException extends ConflictException {

    public CategoriaNomeDuplicadoException() {
        super("Já existe categoria cadastrada com esse nome.");
    }
}
