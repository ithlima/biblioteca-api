package com.mej.biblioteca.dto;

import com.mej.biblioteca.model.Role;

public record AuthResponse(
        Long id,
        String nomeCompleto,
        String email,
        String telefoneWhatsapp,
        Role role,
        String token
) {
}
