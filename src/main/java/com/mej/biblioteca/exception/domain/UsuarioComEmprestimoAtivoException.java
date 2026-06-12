package com.mej.biblioteca.exception.domain;

import com.mej.biblioteca.exception.BusinessException;

public class UsuarioComEmprestimoAtivoException extends BusinessException {

    public UsuarioComEmprestimoAtivoException() {
        super("Leitor já possui empréstimo ativo.");
    }
}
