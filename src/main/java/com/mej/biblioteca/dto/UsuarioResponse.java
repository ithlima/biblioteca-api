package com.mej.biblioteca.dto;

import com.mej.biblioteca.model.Role;
import com.mej.biblioteca.model.Usuario;
import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nomeCompleto,
        String email,
        String telefoneWhatsapp,
        Role role,
        Boolean ativo,
        Boolean loginBloqueado,
        LocalDateTime criadoEm
) {
    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                usuario.getTelefoneWhatsapp(),
                usuario.getRole(),
                usuario.getAtivo(),
                usuario.getLoginBloqueado(),
                usuario.getCriadoEm()
        );
    }
}
