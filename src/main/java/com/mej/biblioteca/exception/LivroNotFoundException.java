package com.mej.biblioteca.exception;

@SuppressWarnings("java:S110")
public class LivroNotFoundException extends NotFoundException {

    public LivroNotFoundException() {
        super("Livro não encontrado.");
    }
}
