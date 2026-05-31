package com.mej.biblioteca.exception;

public class LivroNotFoundException extends NotFoundException {

    public LivroNotFoundException() {
        super("Livro não encontrado.");
    }
}
