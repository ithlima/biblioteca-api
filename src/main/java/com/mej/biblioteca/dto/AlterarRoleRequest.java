package com.mej.biblioteca.dto;

import jakarta.validation.constraints.NotBlank;

public record AlterarRoleRequest(
        @NotBlank String role
) {
}
