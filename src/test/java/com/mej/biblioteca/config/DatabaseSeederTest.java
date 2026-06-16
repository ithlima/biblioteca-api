package com.mej.biblioteca.config;

import com.mej.biblioteca.model.enums.Role;
import com.mej.biblioteca.model.entity.Usuario;
import com.mej.biblioteca.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseSeederTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DatabaseSeeder databaseSeeder;

    @Test
    void run_DeveCriarUsuarioQuandoNaoExistir() throws Exception {
        // Arrange
        when(usuarioRepository.existsByEmail("leitor@biblioteca.com")).thenReturn(false);
        when(passwordEncoder.encode("Leitor@123")).thenReturn("encoded_password");

        // Act
        databaseSeeder.run();

        // Assert
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, times(1)).save(captor.capture());

        Usuario savedUser = captor.getValue();
        assertEquals("Leitor de Testes", savedUser.getNomeCompleto());
        assertEquals("leitor@biblioteca.com", savedUser.getEmail());
        assertEquals("encoded_password", savedUser.getSenha());
        assertEquals(Role.LEITOR, savedUser.getRole());
        assertTrue(savedUser.getAtivo());
    }

    @Test
    void run_NaoDeveCriarUsuarioQuandoJaExistir() throws Exception {
        // Arrange
        when(usuarioRepository.existsByEmail("leitor@biblioteca.com")).thenReturn(true);

        // Act
        databaseSeeder.run();

        // Assert
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}
