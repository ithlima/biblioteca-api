package com.mej.biblioteca.exception;

@SuppressWarnings("java:S110")
public class CategoriaVinculadaLivroException extends ConflictException {

    public CategoriaVinculadaLivroException() {
        super("Não é permitido remover categoria vinculada a livros.");
    }
}
