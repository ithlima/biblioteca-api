package com.mej.biblioteca.exception.domain;

import com.mej.biblioteca.exception.NotFoundException;

public class UsuarioNaoEncontradoException extends NotFoundException {

    public UsuarioNaoEncontradoException() {
        super("Usuário não encontrado.");
    }
}
