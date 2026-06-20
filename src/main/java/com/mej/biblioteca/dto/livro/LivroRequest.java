package com.mej.biblioteca.dto.livro;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.UUID;

public record LivroRequest(
        @NotBlank String nomeObra,
        @NotBlank String autor,
        @NotBlank(message = "A editora não pode estar em branco") String editora,
        @Pattern(
                regexp = "^\\d*$",
                message = "O volume deve conter apenas números."
        )
        String volume,
        @NotBlank(message = "A descrição não pode estar em branco") String descricao,
        List<UUID> categoriasIds,
        @NotNull @Min(0) Integer quantidade,
        @Pattern(
                regexp = "^https://res\\.cloudinary\\.com/.*\\.webp$",
                message = "A capa deve ser uma URL .webp do Cloudinary."
        )
        String fotoCapaUrl
) {
}
