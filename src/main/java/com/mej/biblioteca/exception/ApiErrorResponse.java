package com.mej.biblioteca.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String path,
        List<FieldErrorResponse> campos
) {
}
