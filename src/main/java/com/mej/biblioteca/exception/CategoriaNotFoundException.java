package com.mej.biblioteca.exception;

public class CategoriaNotFoundException extends NotFoundException {

    public CategoriaNotFoundException() {
        super("Categoria não encontrada.");
    }

    public CategoriaNotFoundException(String mensagem) {
        super(mensagem);
    }
}
