package com.mej.biblioteca.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.mej.biblioteca.exception.domain.UltimoAdministradorException;
import com.mej.biblioteca.model.enums.Role;
import com.mej.biblioteca.model.entity.Usuario;
import com.mej.biblioteca.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    private static final String MENSAGEM =
            "Não é permitido remover os privilégios do último administrador do sistema.";

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    void naoRebaixaUltimoAdministradorAtivo() {
        UUID id = UUID.randomUUID();
        Usuario admin = administradorAtivo(id);
        when(usuarioRepository.findAllAtivosByRoleForUpdate(Role.ADMIN)).thenReturn(List.of(admin));
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(admin));

        UltimoAdministradorException exception =
                assertThrows(UltimoAdministradorException.class, () -> service().rebaixarLeitor(id));

        assertEquals(MENSAGEM, exception.getMessage());
    }

    @Test
    void naoBloqueiaUltimoAdministradorAtivo() {
        UUID id = UUID.randomUUID();
        Usuario admin = administradorAtivo(id);
        when(usuarioRepository.findAllAtivosByRoleForUpdate(Role.ADMIN)).thenReturn(List.of(admin));
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(admin));

        UltimoAdministradorException exception =
                assertThrows(UltimoAdministradorException.class, () -> service().bloquear(id));

        assertEquals(MENSAGEM, exception.getMessage());
    }

    private UsuarioService service() {
        return new UsuarioService(usuarioRepository);
    }

    private Usuario administradorAtivo(UUID id) {
        return Usuario.builder()
                .id(id)
                .role(Role.ADMIN)
                .ativo(true)
                .loginBloqueado(false)
                .build();
    }
}
