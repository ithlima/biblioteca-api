package com.mej.biblioteca.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record EmprestimoSolicitarRequest(
        @NotNull UUID livroId
) {
}
