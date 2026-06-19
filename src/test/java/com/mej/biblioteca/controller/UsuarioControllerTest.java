package com.mej.biblioteca.controller;

import com.mej.biblioteca.dto.usuario.UsuarioResponse;
import com.mej.biblioteca.model.enums.Role;
import com.mej.biblioteca.model.entity.Usuario;
import com.mej.biblioteca.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("java:S8692")
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @Test
    void testMeuPerfil() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        Usuario usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .nomeCompleto("Usuário Teste")
                .email("teste@teste.com")
                .telefoneWhatsapp("11999999999")
                .role(Role.LEITOR)
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .build();

        when(usuarioService.usuarioAutenticado(authentication)).thenReturn(usuario);

        // Act
        UsuarioResponse response = usuarioController.meuPerfil(authentication);

        // Assert
        assertNotNull(response);
        assertEquals("Usuário Teste", response.nomeCompleto());
        assertEquals("teste@teste.com", response.email());
        assertEquals(Role.LEITOR, response.role());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testListar() {
        org.springframework.data.domain.Pageable pageable = mock(org.springframework.data.domain.Pageable.class);
        org.springframework.data.domain.Page<UsuarioResponse> mockPage = mock(org.springframework.data.domain.Page.class);
        when(usuarioService.listar(null, null, null, pageable)).thenReturn(mockPage);

        var result = usuarioController.listar(null, null, null, pageable);
        assertEquals(mockPage, result);
    }

    @Test
    void testPromoverAdmin() {
        UUID id = UUID.randomUUID();
        UsuarioResponse mockResponse = mock(UsuarioResponse.class);
        when(usuarioService.promoverAdmin(id)).thenReturn(mockResponse);

        var result = usuarioController.promoverAdmin(id);
        assertEquals(mockResponse, result);
    }

    @Test
    void testRebaixarLeitor() {
        UUID id = UUID.randomUUID();
        UsuarioResponse mockResponse = mock(UsuarioResponse.class);
        when(usuarioService.rebaixarLeitor(id)).thenReturn(mockResponse);

        var result = usuarioController.rebaixarLeitor(id);
        assertEquals(mockResponse, result);
    }

    @Test
    void testAlterarRole() {
        UUID id = UUID.randomUUID();
        com.mej.biblioteca.dto.usuario.AlterarRoleRequest req = new com.mej.biblioteca.dto.usuario.AlterarRoleRequest("ADMIN");
        UsuarioResponse mockResponse = mock(UsuarioResponse.class);
        when(usuarioService.alterarRole(id, "ADMIN")).thenReturn(mockResponse);

        var result = usuarioController.alterarRole(id, req);
        assertEquals(mockResponse, result);
    }

    @Test
    void testBloquear() {
        UUID id = UUID.randomUUID();
        com.mej.biblioteca.dto.usuario.UsuarioBloquearRequest req = new com.mej.biblioteca.dto.usuario.UsuarioBloquearRequest("Motivo");
        UsuarioResponse mockResponse = mock(UsuarioResponse.class);
        when(usuarioService.bloquear(id, req)).thenReturn(mockResponse);

        var result = usuarioController.bloquear(id, req);
        assertEquals(mockResponse, result);
    }

    @Test
    void testDesbloquear() {
        UUID id = UUID.randomUUID();
        UsuarioResponse mockResponse = mock(UsuarioResponse.class);
        when(usuarioService.desbloquear(id)).thenReturn(mockResponse);

        var result = usuarioController.desbloquear(id);
        assertEquals(mockResponse, result);
    }
}
