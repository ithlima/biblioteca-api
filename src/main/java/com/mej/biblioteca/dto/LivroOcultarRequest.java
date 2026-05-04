package com.mej.biblioteca.dto;

import jakarta.validation.constraints.NotBlank;

public record LivroOcultarRequest(
        @NotBlank String motivoOcultacao
) {
}
