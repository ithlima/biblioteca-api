package com.mej.biblioteca.security;

import com.mej.biblioteca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String identificador) throws UsernameNotFoundException {
        var usuario = usuarioRepository.findByEmailOrTelefoneWhatsapp(identificador, identificador)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado."));

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .roles(usuario.getRole().name())
                .disabled(!usuario.getAtivo())
                .accountLocked(usuario.getLoginBloqueado())
                .build();
    }
}
