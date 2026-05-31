package com.mej.biblioteca.exception;

public class UltimoAdministradorException extends AlteracaoRoleNaoPermitidaException {

    public UltimoAdministradorException() {
        super("Não é permitido remover os privilégios do último administrador do sistema.");
    }
}
