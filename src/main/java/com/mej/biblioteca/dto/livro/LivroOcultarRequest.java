package com.mej.biblioteca.dto.livro;

import jakarta.validation.constraints.NotBlank;

public record LivroOcultarRequest(
        @NotBlank String motivoOcultacao
) {
}
