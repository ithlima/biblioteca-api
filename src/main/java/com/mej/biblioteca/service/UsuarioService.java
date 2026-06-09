package com.mej.biblioteca.service;

import com.mej.biblioteca.dto.UsuarioResponse;
import com.mej.biblioteca.exception.AlteracaoRoleNaoPermitidaException;
import com.mej.biblioteca.exception.RoleInvalidaException;
import com.mej.biblioteca.exception.UltimoAdministradorException;
import com.mej.biblioteca.exception.UsuarioNaoEncontradoException;
import com.mej.biblioteca.model.Role;
import com.mej.biblioteca.model.Usuario;
import com.mej.biblioteca.repository.UsuarioRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@SuppressWarnings("java:S6809")
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(UsuarioNaoEncontradoException::new);
    }

    @Transactional(readOnly = true)
    public Usuario usuarioAutenticado(Authentication authentication) {
        return buscarPorEmail(authentication.getName());
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listar(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(UsuarioResponse::from);
    }

    @Transactional
    public UsuarioResponse promoverAdmin(UUID id) {
        Usuario usuario = buscarPorId(id);
        if (usuario.getRole() == Role.ADMIN) {
            throw new AlteracaoRoleNaoPermitidaException("Usuário já possui role ADMIN.");
        }
        usuario.setRole(Role.ADMIN);
        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public UsuarioResponse rebaixarLeitor(UUID id) {
        List<Usuario> administradoresAtivos = usuarioRepository.findAllAtivosByRoleForUpdate(Role.ADMIN);
        Usuario usuario = buscarPorId(id);
        if (usuario.getRole() == Role.LEITOR) {
            throw new AlteracaoRoleNaoPermitidaException("Usuário já possui role LEITOR.");
        }
        if (administradorAtivo(usuario) && administradoresAtivos.size() <= 1) {
            throw new UltimoAdministradorException();
        }
        usuario.setRole(Role.LEITOR);
        return UsuarioResponse.from(usuario);
    }

    @Transactional
    public UsuarioResponse alterarRole(UUID id, String role) {
        Role roleDestino = parseRole(role);
        return switch (roleDestino) {
            case ADMIN -> promoverAdmin(id);
            case LEITOR -> rebaixarLeitor(id);
        };
    }

    @Transactional
    public UsuarioResponse bloquear(UUID id) {
        List<Usuario> administradoresAtivos = usuarioRepository.findAllAtivosByRoleForUpdate(Role.ADMIN);
        Usuario usuario = buscarPorId(id);
        if (administradorAtivo(usuario) && administradoresAtivos.size() <= 1) {
            throw new UltimoAdministradorException();
        }
        usuario.setLoginBloqueado(true);
        return UsuarioResponse.from(usuario);
    }

    private boolean administradorAtivo(Usuario usuario) {
        return usuario.getRole() == Role.ADMIN
                && Boolean.TRUE.equals(usuario.getAtivo())
                && !Boolean.TRUE.equals(usuario.getLoginBloqueado());
    }

    private Usuario buscarPorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(UsuarioNaoEncontradoException::new);
    }

    @Transactional
    public UsuarioResponse desbloquear(UUID id) {
        Usuario usuario = buscarPorId(id);
        usuario.setLoginBloqueado(false);
        return UsuarioResponse.from(usuario);
    }

    private Role parseRole(String role) {
        try {
            return Role.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new RoleInvalidaException();
        }
    }
}
