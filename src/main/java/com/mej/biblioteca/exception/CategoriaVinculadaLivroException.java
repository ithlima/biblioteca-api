package com.mej.biblioteca.exception;

public class CategoriaVinculadaLivroException extends ConflictException {

    public CategoriaVinculadaLivroException() {
        super("Não é permitido remover categoria vinculada a livros.");
    }
}
