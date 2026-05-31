package com.mej.biblioteca.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mej.biblioteca.dto.ReenviarCodigoRequest;
import com.mej.biblioteca.exception.ConflictException;
import com.mej.biblioteca.exception.UsuarioNotFoundException;
import com.mej.biblioteca.model.TipoCodigoVerificacao;
import com.mej.biblioteca.model.Usuario;
import com.mej.biblioteca.repository.UsuarioRepository;
import com.mej.biblioteca.security.CustomUserDetailsService;
import com.mej.biblioteca.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private CodigoVerificacaoService codigoVerificacaoService;

    @Test
    void reenviaCodigoParaUsuarioAindaNaoConfirmado() {
        when(usuarioRepository.findByEmail("usuario@example.com"))
                .thenReturn(Optional.of(Usuario.builder().emailValidado(false).build()));

        service().reenviarCodigo(new ReenviarCodigoRequest(" Usuario@Example.com "));

        verify(codigoVerificacaoService)
                .gerarEEnviar("usuario@example.com", TipoCodigoVerificacao.CADASTRO);
    }

    @Test
    void naoReenviaCodigoParaUsuarioConfirmado() {
        when(usuarioRepository.findByEmail("usuario@example.com"))
                .thenReturn(Optional.of(Usuario.builder().emailValidado(true).build()));

        assertThrows(
                ConflictException.class,
                () -> service().reenviarCodigo(new ReenviarCodigoRequest("usuario@example.com"))
        );
    }

    @Test
    void informaQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findByEmail("usuario@example.com")).thenReturn(Optional.empty());

        assertThrows(
                UsuarioNotFoundException.class,
                () -> service().reenviarCodigo(new ReenviarCodigoRequest("usuario@example.com"))
        );
    }

    private AuthService service() {
        return new AuthService(
                usuarioRepository,
                passwordEncoder,
                authenticationManager,
                userDetailsService,
                jwtService,
                codigoVerificacaoService
        );
    }
}
