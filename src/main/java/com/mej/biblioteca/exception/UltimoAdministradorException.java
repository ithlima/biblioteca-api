package com.mej.biblioteca.exception;

public class UltimoAdministradorException extends AlteracaoRoleNaoPermitidaException {

    public UltimoAdministradorException() {
        super("Não é permitido rebaixar o último administrador do sistema.");
    }
}
