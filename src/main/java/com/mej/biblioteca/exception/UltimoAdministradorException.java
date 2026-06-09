package com.mej.biblioteca.exception;

@SuppressWarnings("java:S110")
public class UltimoAdministradorException extends AlteracaoRoleNaoPermitidaException {

    public UltimoAdministradorException() {
        super("Não é permitido remover os privilégios do último administrador do sistema.");
    }
}
