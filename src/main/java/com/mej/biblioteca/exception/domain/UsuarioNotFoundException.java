package com.mej.biblioteca.exception.domain;

import com.mej.biblioteca.exception.NotFoundException;

public class UsuarioNotFoundException extends NotFoundException {

    public UsuarioNotFoundException() {
        super("Usuário não encontrado.");
    }
}
