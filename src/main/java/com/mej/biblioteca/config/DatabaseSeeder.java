package com.mej.biblioteca.config;

import com.mej.biblioteca.model.Role;
import com.mej.biblioteca.model.Usuario;
import com.mej.biblioteca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @SuppressWarnings("java:S6437")
    public void run(String... args) throws Exception {
        if (!usuarioRepository.existsByEmail("leitor@biblioteca.com")) {
            log.info("Criando usuário Leitor padrão para testes (leitor@biblioteca.com)...");
            Usuario leitor = Usuario.builder()
                    .nomeCompleto("Leitor de Testes")
                    .email("leitor@biblioteca.com")
                    .telefoneWhatsapp("85900000000")
                    .senha(passwordEncoder.encode("Leitor@123"))
                    .role(Role.LEITOR)
                    .ativo(true)
                    .loginBloqueado(false)
                    .emailValidado(true)
                    .build();
            usuarioRepository.save(leitor);
        }
    }
}
