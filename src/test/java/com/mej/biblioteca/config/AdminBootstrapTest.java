package com.mej.biblioteca.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mej.biblioteca.model.Role;
import com.mej.biblioteca.model.Usuario;
import com.mej.biblioteca.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AdminBootstrapTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Environment environment;

    @Test
    void naoCriaAdminQuandoJaExisteAdministrador() {
        when(usuarioRepository.existsByRole(Role.ADMIN)).thenReturn(true);

        bootstrap().run(null);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void naoCriaAdminQuandoVariaveisNaoEstaoConfiguradas() {
        when(usuarioRepository.existsByRole(Role.ADMIN)).thenReturn(false);

        bootstrap().run(null);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void criaPrimeiroAdminComVariaveisDeAmbienteEPasswordEncoder() {
        when(usuarioRepository.existsByRole(Role.ADMIN)).thenReturn(false);
        when(environment.getProperty("ADMIN_EMAIL")).thenReturn(" ADMIN@EXAMPLE.COM ");
        when(environment.getProperty("ADMIN_PASSWORD")).thenReturn("senha-externa");
        when(usuarioRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha-externa")).thenReturn("senha-codificada");

        bootstrap().run(null);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario admin = captor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals("admin@example.com", admin.getEmail());
        org.junit.jupiter.api.Assertions.assertEquals("senha-codificada", admin.getSenha());
        org.junit.jupiter.api.Assertions.assertEquals(Role.ADMIN, admin.getRole());
    }

    private AdminBootstrap bootstrap() {
        return new AdminBootstrap(usuarioRepository, passwordEncoder, environment);
    }
}
