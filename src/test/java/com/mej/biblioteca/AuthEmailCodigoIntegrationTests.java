package com.mej.biblioteca;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mej.biblioteca.dto.AuthCadastroRequest;
import com.mej.biblioteca.dto.ConfirmarAlteracaoSenhaRequest;
import com.mej.biblioteca.dto.ConfirmarCadastroRequest;
import com.mej.biblioteca.exception.BusinessException;
import com.mej.biblioteca.model.CodigoVerificacao;
import com.mej.biblioteca.model.Role;
import com.mej.biblioteca.model.TipoCodigoVerificacao;
import com.mej.biblioteca.model.Usuario;
import com.mej.biblioteca.repository.CodigoVerificacaoRepository;
import com.mej.biblioteca.repository.UsuarioRepository;
import com.mej.biblioteca.service.AuthService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class AuthEmailCodigoIntegrationTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CodigoVerificacaoRepository codigoVerificacaoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        codigoVerificacaoRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void cadastrarCriaUsuarioPendenteECodigoDeCadastro() {
        var response = authService.cadastrar(new AuthCadastroRequest(
                "Maria Leitora",
                "Maria@Email.com",
                "85999999999",
                "Senha@123"
        ));

        Usuario usuario = usuarioRepository.findByEmail("maria@email.com").orElseThrow();
        var codigo = codigoVerificacaoRepository
                .findFirstByEmailAndTipoAndUsadoEmIsNullOrderByCriadoEmDesc("maria@email.com", TipoCodigoVerificacao.CADASTRO);

        assertThat(response.token()).isNull();
        assertThat(usuario.getAtivo()).isFalse();
        assertThat(usuario.getLoginBloqueado()).isTrue();
        assertThat(usuario.getEmailValidado()).isFalse();
        assertThat(codigo).isPresent();
        assertThat(codigo.get().getExpiraEm()).isAfter(LocalDateTime.now());
    }

    @Test
    void confirmarCadastroComCodigoValidoAtivaUsuarioERetornaToken() {
        criarUsuario("leitor@email.com", "Senha@123", false, true, false);
        criarCodigo("leitor@email.com", TipoCodigoVerificacao.CADASTRO, "123456", LocalDateTime.now().plusMinutes(5));

        var response = authService.confirmarCadastro(new ConfirmarCadastroRequest("leitor@email.com", "123456"));

        Usuario usuario = usuarioRepository.findByEmail("leitor@email.com").orElseThrow();
        assertThat(usuario.getAtivo()).isTrue();
        assertThat(usuario.getLoginBloqueado()).isFalse();
        assertThat(usuario.getEmailValidado()).isTrue();
        assertThat(response.token()).isNotBlank();
    }

    @Test
    void confirmarAlteracaoSenhaComCodigoValidoAtualizaSenha() {
        criarUsuario("senha@email.com", "Senha@123", true, false, true);
        criarCodigo("senha@email.com", TipoCodigoVerificacao.ALTERACAO_SENHA, "654321", LocalDateTime.now().plusMinutes(5));

        authService.confirmarAlteracaoSenha(new ConfirmarAlteracaoSenhaRequest(
                "senha@email.com",
                "654321",
                "Nova@123"
        ));

        Usuario usuario = usuarioRepository.findByEmail("senha@email.com").orElseThrow();
        assertThat(passwordEncoder.matches("Nova@123", usuario.getSenha())).isTrue();
        assertThat(passwordEncoder.matches("Senha@123", usuario.getSenha())).isFalse();
    }

    @Test
    void confirmarCadastroComCodigoExpiradoFalha() {
        criarUsuario("expirado@email.com", "Senha@123", false, true, false);
        criarCodigo("expirado@email.com", TipoCodigoVerificacao.CADASTRO, "123456", LocalDateTime.now().minusMinutes(1));

        assertThatThrownBy(() -> authService.confirmarCadastro(new ConfirmarCadastroRequest("expirado@email.com", "123456")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Codigo de verificacao invalido ou expirado.");
    }

    private void criarUsuario(String email, String senha, boolean ativo, boolean loginBloqueado, boolean emailValidado) {
        usuarioRepository.save(Usuario.builder()
                .nomeCompleto("Usuario Teste")
                .email(email)
                .telefoneWhatsapp("85988887777")
                .senha(passwordEncoder.encode(senha))
                .role(Role.LEITOR)
                .ativo(ativo)
                .loginBloqueado(loginBloqueado)
                .emailValidado(emailValidado)
                .build());
    }

    private void criarCodigo(String email, TipoCodigoVerificacao tipo, String codigo, LocalDateTime expiraEm) {
        codigoVerificacaoRepository.save(CodigoVerificacao.builder()
                .email(email)
                .codigoHash(passwordEncoder.encode(codigo))
                .tipo(tipo)
                .expiraEm(expiraEm)
                .build());
    }
}
