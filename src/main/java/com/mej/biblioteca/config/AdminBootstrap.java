package com.mej.biblioteca.config;

import com.mej.biblioteca.model.Role;
import com.mej.biblioteca.model.Usuario;
import com.mej.biblioteca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {

    private static final String ADMIN_EMAIL = "admin@biblioteca.com";
    private static final String ADMIN_PASSWORD = "Admin@123";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usuarioRepository.existsByRole(Role.ADMIN)) {
            log.info("ADMIN já existente. Bootstrap automático não criou novo administrador.");
            return;
        }

        Usuario admin = usuarioRepository.findByEmail(ADMIN_EMAIL)
                .map(this::promoverUsuarioExistente)
                .orElseGet(this::novoAdministrador);
        usuarioRepository.save(admin);
        log.info("ADMIN criado automaticamente com e-mail {}.", ADMIN_EMAIL);
    }

    private Usuario promoverUsuarioExistente(Usuario usuario) {
        usuario.setRole(Role.ADMIN);
        usuario.setAtivo(true);
        usuario.setLoginBloqueado(false);
        usuario.setEmailValidado(true);
        usuario.setSenha(passwordEncoder.encode(ADMIN_PASSWORD));
        return usuario;
    }

    private Usuario novoAdministrador() {
        return Usuario.builder()
                .nomeCompleto("Administrador Inicial")
                .email(ADMIN_EMAIL)
                .senha(passwordEncoder.encode(ADMIN_PASSWORD))
                .role(Role.ADMIN)
                .ativo(true)
                .loginBloqueado(false)
                .emailValidado(true)
                .build();
    }
}
