package com.mej.biblioteca.exception;

public class UsuarioComPenalidadeAtivaException extends BusinessException {

    public UsuarioComPenalidadeAtivaException() {
        super("Leitor possui penalidade ativa e não pode solicitar empréstimos.");
    }
}
