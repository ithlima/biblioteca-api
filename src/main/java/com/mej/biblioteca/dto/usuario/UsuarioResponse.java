package com.mej.biblioteca.dto.usuario;

import com.mej.biblioteca.model.enums.Role;
import com.mej.biblioteca.model.entity.Usuario;
import java.time.LocalDateTime;
import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        String nomeCompleto,
        String email,
        String telefoneWhatsapp,
        Role role,
        Boolean ativo,
        Boolean loginBloqueado,
        Boolean emailValidado,
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
                usuario.getEmailValidado(),
                usuario.getCriadoEm()
        );
    }
}
