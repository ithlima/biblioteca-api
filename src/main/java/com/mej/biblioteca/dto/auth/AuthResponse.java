package com.mej.biblioteca.dto.auth;

import com.mej.biblioteca.model.enums.Role;
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
