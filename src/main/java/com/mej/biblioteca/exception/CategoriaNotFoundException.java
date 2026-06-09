package com.mej.biblioteca.exception;

@SuppressWarnings("java:S110")
public class CategoriaNotFoundException extends NotFoundException {

    public CategoriaNotFoundException() {
        super("Categoria não encontrada.");
    }

    public CategoriaNotFoundException(String mensagem) {
        super(mensagem);
    }
}
