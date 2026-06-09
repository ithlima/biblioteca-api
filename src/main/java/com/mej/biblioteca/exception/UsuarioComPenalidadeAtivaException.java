package com.mej.biblioteca.exception;

@SuppressWarnings("java:S110")
public class UsuarioComPenalidadeAtivaException extends BusinessException {

    public UsuarioComPenalidadeAtivaException() {
        super("Leitor possui penalidade ativa e não pode solicitar empréstimos.");
    }
}
