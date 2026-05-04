package com.mej.biblioteca.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LivroRequest(
        @NotBlank String nomeObra,
        @NotBlank String autor,
        String editora,
        String volume,
        String descricao,
        String categorias,
        @NotNull @Min(0) Integer quantidade,
        String fotoCapaUrl
) {
}
