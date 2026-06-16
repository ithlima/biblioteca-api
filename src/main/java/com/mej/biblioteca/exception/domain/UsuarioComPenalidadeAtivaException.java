package com.mej.biblioteca.exception.domain;

import com.mej.biblioteca.exception.BusinessException;

public class UsuarioComPenalidadeAtivaException extends BusinessException {

    public UsuarioComPenalidadeAtivaException() {
        super("Leitor possui penalidade ativa e não pode solicitar empréstimos.");
    }
}
