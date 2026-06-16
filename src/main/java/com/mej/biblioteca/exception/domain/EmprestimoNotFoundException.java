package com.mej.biblioteca.exception.domain;

import com.mej.biblioteca.exception.NotFoundException;

public class EmprestimoNotFoundException extends NotFoundException {

    public EmprestimoNotFoundException() {
        super("Empréstimo não encontrado.");
    }
}
