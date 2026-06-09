package com.mej.biblioteca.exception;

@SuppressWarnings("java:S110")
public class EmprestimoNotFoundException extends NotFoundException {

    public EmprestimoNotFoundException() {
        super("Empréstimo não encontrado.");
    }
}
