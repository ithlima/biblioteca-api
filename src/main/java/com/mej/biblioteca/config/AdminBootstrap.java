package com.mej.biblioteca.config;

import com.mej.biblioteca.model.Role;
import com.mej.biblioteca.model.Usuario;
import com.mej.biblioteca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usuarioRepository.existsByRole(Role.ADMIN)) {
            log.info("ADMIN já existente. Bootstrap automático não criou novo administrador.");
            return;
        }

        String email = environment.getProperty("ADMIN_EMAIL");
        String password = environment.getProperty("ADMIN_PASSWORD");
        if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
            log.warn("Nenhum ADMIN existente e ADMIN_EMAIL ou ADMIN_PASSWORD não configurado. Bootstrap não criou administrador.");
            return;
        }

        String emailNormalizado = email.trim().toLowerCase();
        Usuario admin = usuarioRepository.findByEmail(emailNormalizado)
                .map(usuario -> promoverUsuarioExistente(usuario, password))
                .orElseGet(() -> novoAdministrador(emailNormalizado, password));
        usuarioRepository.save(admin);
        log.info("ADMIN criado automaticamente com e-mail {}.", emailNormalizado);
    }

    private Usuario promoverUsuarioExistente(Usuario usuario, String password) {
        usuario.setRole(Role.ADMIN);
        usuario.setAtivo(true);
        usuario.setLoginBloqueado(false);
        usuario.setEmailValidado(true);
        usuario.setSenha(passwordEncoder.encode(password));
        return usuario;
    }

    private Usuario novoAdministrador(String email, String password) {
        return Usuario.builder()
                .nomeCompleto("Administrador Inicial")
                .email(email)
                .senha(passwordEncoder.encode(password))
                .role(Role.ADMIN)
                .ativo(true)
                .loginBloqueado(false)
                .emailValidado(true)
                .build();
    }
}
