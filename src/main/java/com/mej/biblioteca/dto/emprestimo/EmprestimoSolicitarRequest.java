package com.mej.biblioteca.dto.emprestimo;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record EmprestimoSolicitarRequest(
        @NotNull UUID livroId
) {
}
