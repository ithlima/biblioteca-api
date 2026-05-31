package com.mej.biblioteca.exception;

public class UsuarioComEmprestimoAtivoException extends BusinessException {

    public UsuarioComEmprestimoAtivoException() {
        super("Leitor já possui empréstimo ativo.");
    }
}
