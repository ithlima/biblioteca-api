package com.mej.biblioteca.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SolicitarAlteracaoSenhaRequest(
        @NotBlank @Email String email
) {
}
