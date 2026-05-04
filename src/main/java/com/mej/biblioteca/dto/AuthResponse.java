package com.mej.biblioteca.dto;

import com.mej.biblioteca.model.Role;
import java.util.UUID;

public record AuthResponse(
        UUID id,
        String nomeCompleto,
        String email,
        String telefoneWhatsapp,
        Role role,
        String token
) {
}
