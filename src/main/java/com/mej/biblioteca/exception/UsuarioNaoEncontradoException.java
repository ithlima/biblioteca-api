package com.mej.biblioteca.exception;

public class UsuarioNaoEncontradoException extends NotFoundException {

    public UsuarioNaoEncontradoException() {
        super("Usuário não encontrado.");
    }
}
