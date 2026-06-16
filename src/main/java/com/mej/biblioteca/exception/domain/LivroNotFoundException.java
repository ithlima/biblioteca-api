package com.mej.biblioteca.exception.domain;

import com.mej.biblioteca.exception.NotFoundException;

public class LivroNotFoundException extends NotFoundException {

    public LivroNotFoundException() {
        super("Livro não encontrado.");
    }
}
