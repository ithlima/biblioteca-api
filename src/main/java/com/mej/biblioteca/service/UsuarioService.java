package com.mej.biblioteca.service;

import com.mej.biblioteca.dto.UsuarioResponse;
import com.mej.biblioteca.exception.NotFoundException;
import com.mej.biblioteca.model.Role;
import com.mej.biblioteca.model.Usuario;
import com.mej.biblioteca.repository.UsuarioRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado."));
    }

    @Transactional(readOnly = true)
    public Usuario usuarioAutenticado(Authentication authentication) {
        return buscarPorEmail(authentication.getName());
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponse::from)
                .toList();
    }

    @Transactional
    public UsuarioResponse promoverAdmin(UUID id) {
        Usuario usuario = buscarPorId(id);
        usuario.setRole(Role.ADMIN);
        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public UsuarioResponse rebaixarLeitor(UUID id) {
        Usuario usuario = buscarPorId(id);
        usuario.setRole(Role.LEITOR);
        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public UsuarioResponse bloquear(UUID id) {
        Usuario usuario = buscarPorId(id);
        usuario.setLoginBloqueado(true);
        return UsuarioResponse.from(usuario);
    }

    private Usuario buscarPorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado."));
    }
}
