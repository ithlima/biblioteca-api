package com.mej.biblioteca.exception.domain;

import com.mej.biblioteca.exception.NotFoundException;

public class CategoriaNotFoundException extends NotFoundException {

    public CategoriaNotFoundException() {
        super("Categoria não encontrada.");
    }

    public CategoriaNotFoundException(String mensagem) {
        super(mensagem);
    }
}
