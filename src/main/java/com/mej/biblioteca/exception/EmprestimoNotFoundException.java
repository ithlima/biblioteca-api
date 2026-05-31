package com.mej.biblioteca.exception;

public class EmprestimoNotFoundException extends NotFoundException {

    public EmprestimoNotFoundException() {
        super("Empréstimo não encontrado.");
    }
}
