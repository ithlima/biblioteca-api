package com.mej.biblioteca.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mej.biblioteca.exception.domain.UltimoAdministradorException;
import com.mej.biblioteca.model.enums.Role;
import com.mej.biblioteca.model.entity.Usuario;
import com.mej.biblioteca.repository.UsuarioRepository;
import com.mej.biblioteca.dto.usuario.UsuarioResponse;
import com.mej.biblioteca.dto.usuario.UsuarioBloquearRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    private static final String MENSAGEM = "Não é permitido remover os privilégios do último administrador do sistema.";

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    void naoRebaixaUltimoAdministradorAtivo() {
        UUID id = UUID.randomUUID();
        Usuario admin = administradorAtivo(id);
        when(usuarioRepository.findAllAtivosByRoleForUpdate(Role.ADMIN)).thenReturn(List.of(admin));
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(admin));

        UltimoAdministradorException exception = assertThrows(UltimoAdministradorException.class,
                () -> service().rebaixarLeitor(id));

        assertEquals(MENSAGEM, exception.getMessage());
    }

    @Test
    void naoBloqueiaUltimoAdministradorAtivo() {
        UUID id = UUID.randomUUID();
        Usuario admin = administradorAtivo(id);
        when(usuarioRepository.findAllAtivosByRoleForUpdate(Role.ADMIN)).thenReturn(List.of(admin));
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(admin));

        UltimoAdministradorException exception = assertThrows(UltimoAdministradorException.class,
                () -> service().bloquear(id, null));

        assertEquals(MENSAGEM, exception.getMessage());
    }

    @Test
    void bloqueiaUsuarioEAdicionaMotivo() {
        UUID id = UUID.randomUUID();
        Usuario leitor = leitorAtivo(id);
        when(usuarioRepository.findAllAtivosByRoleForUpdate(Role.ADMIN))
                .thenReturn(List.of(administradorAtivo(UUID.randomUUID())));
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(leitor));

        UsuarioBloquearRequest request = new UsuarioBloquearRequest("Violação de regras");
        UsuarioResponse response = service().bloquear(id, request);

        assertTrue(response.loginBloqueado());
        assertEquals("Violação de regras", response.motivoBloqueio());
        assertTrue(leitor.getLoginBloqueado());
        assertEquals("Violação de regras", leitor.getMotivoBloqueio());
    }

    @Test
    void desbloqueiaUsuarioERemoveMotivo() {
        UUID id = UUID.randomUUID();
        Usuario leitor = leitorAtivo(id);
        leitor.setLoginBloqueado(true);
        leitor.setMotivoBloqueio("Violação de regras");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(leitor));

        UsuarioResponse response = service().desbloquear(id);

        assertFalse(response.loginBloqueado());
        assertNull(response.motivoBloqueio());
        assertFalse(leitor.getLoginBloqueado());
        assertNull(leitor.getMotivoBloqueio());
    }

    @Test
    void listaUsuariosComFiltros() {
        Role role = Role.LEITOR;
        Boolean ativo = true;
        Boolean loginBloqueado = false;
        Pageable pageable = PageRequest.of(0, 10);
        Usuario leitor = leitorAtivo(UUID.randomUUID());

        when(usuarioRepository.findComFiltros(role, ativo, loginBloqueado, pageable))
                .thenReturn(new PageImpl<>(List.of(leitor)));

        Page<UsuarioResponse> result = service().listar(role, ativo, loginBloqueado, pageable);

        assertEquals(1, result.getTotalElements());
        verify(usuarioRepository).findComFiltros(role, ativo, loginBloqueado, pageable);
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

    private Usuario leitorAtivo(UUID id) {
        return Usuario.builder()
                .id(id)
                .role(Role.LEITOR)
                .ativo(true)
                .loginBloqueado(false)
                .build();
    }
}
