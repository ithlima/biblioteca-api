package com.mej.biblioteca.exception;

@SuppressWarnings("java:S110")
public class UsuarioComEmprestimoAtivoException extends BusinessException {

    public UsuarioComEmprestimoAtivoException() {
        super("Leitor já possui empréstimo ativo.");
    }
}
