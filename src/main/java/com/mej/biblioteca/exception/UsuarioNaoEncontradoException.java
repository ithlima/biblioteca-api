package com.mej.biblioteca.exception;

@SuppressWarnings("java:S110")
public class UsuarioNaoEncontradoException extends NotFoundException {

    public UsuarioNaoEncontradoException() {
        super("Usuário não encontrado.");
    }
}
