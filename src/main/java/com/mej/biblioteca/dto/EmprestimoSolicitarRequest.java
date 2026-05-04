package com.mej.biblioteca.dto;

import jakarta.validation.constraints.NotNull;

public record EmprestimoSolicitarRequest(
        @NotNull Long livroId
) {
}
