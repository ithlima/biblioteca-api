package com.mej.biblioteca.dto.usuario;

import jakarta.validation.constraints.Size;

public record UsuarioBloquearRequest(
        @Size(max = 500, message = "Motivo de bloqueio deve ter no máximo 500 caracteres")
        String motivoBloqueio
) {
}
