package com.mej.biblioteca.exception.domain;

import com.mej.biblioteca.exception.ConflictException;

public class CategoriaVinculadaLivroException extends ConflictException {

    public CategoriaVinculadaLivroException() {
        super("Não é permitido remover categoria vinculada a livros.");
    }
}
