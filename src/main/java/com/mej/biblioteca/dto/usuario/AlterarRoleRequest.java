package com.mej.biblioteca.dto.usuario;

import jakarta.validation.constraints.NotBlank;

public record AlterarRoleRequest(
        @NotBlank String role
) {
}
