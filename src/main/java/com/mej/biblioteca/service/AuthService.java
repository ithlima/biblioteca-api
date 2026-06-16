package com.mej.biblioteca.service;

import com.mej.biblioteca.dto.auth.AuthCadastroRequest;
import com.mej.biblioteca.dto.auth.AuthResponse;
import com.mej.biblioteca.dto.auth.ConfirmarAlteracaoSenhaRequest;
import com.mej.biblioteca.dto.auth.ConfirmarCadastroRequest;
import com.mej.biblioteca.dto.auth.LoginRequest;
import com.mej.biblioteca.dto.auth.ReenviarCodigoRequest;
import com.mej.biblioteca.dto.auth.SolicitarAlteracaoSenhaRequest;
import com.mej.biblioteca.exception.ConflictException;
import com.mej.biblioteca.exception.domain.UsuarioNotFoundException;
import com.mej.biblioteca.model.enums.Role;
import com.mej.biblioteca.model.enums.TipoCodigoVerificacao;
import com.mej.biblioteca.model.entity.Usuario;
import com.mej.biblioteca.repository.UsuarioRepository;
import com.mej.biblioteca.security.CustomUserDetailsService;
import com.mej.biblioteca.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final CodigoVerificacaoService codigoVerificacaoService;

    @Transactional
    public AuthResponse cadastrar(AuthCadastroRequest request) {
        String email = normalizarEmail(request.email());
        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflictException("Já existe usuário cadastrado com este e-mail.");
        }
        if (request.telefoneWhatsapp() != null
                && !request.telefoneWhatsapp().isBlank()
                && usuarioRepository.existsByTelefoneWhatsapp(request.telefoneWhatsapp())) {
            throw new ConflictException("Já existe usuário cadastrado com este telefone.");
        }

        Usuario usuario = Usuario.builder()
                .nomeCompleto(request.nomeCompleto())
                .email(email)
                .telefoneWhatsapp(request.telefoneWhatsapp())
                .senha(passwordEncoder.encode(request.senha()))
                .role(Role.LEITOR)
                .ativo(false)
                .loginBloqueado(true)
                .emailValidado(false)
                .build();

        Usuario salvo = usuarioRepository.save(usuario);
        codigoVerificacaoService.gerarEEnviar(salvo.getEmail(), TipoCodigoVerificacao.CADASTRO);
        return toAuthResponse(salvo, null);
    }

    @Transactional
    public AuthResponse confirmarCadastro(ConfirmarCadastroRequest request) {
        String email = normalizarEmail(request.email());
        codigoVerificacaoService.validar(email, TipoCodigoVerificacao.CADASTRO, request.codigo());
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(UsuarioNotFoundException::new);
        usuario.setEmailValidado(true);
        usuario.setAtivo(true);
        usuario.setLoginBloqueado(false);
        return toAuthResponse(usuario);
    }

    @Transactional
    public void reenviarCodigo(ReenviarCodigoRequest request) {
        String email = normalizarEmail(request.email());
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(UsuarioNotFoundException::new);
        if (Boolean.TRUE.equals(usuario.getEmailValidado())) {
            throw new ConflictException("Usuário já está confirmado.");
        }
        codigoVerificacaoService.gerarEEnviar(email, TipoCodigoVerificacao.CADASTRO);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.identificador(), request.senha())
        );

        Usuario usuario = usuarioRepository.findByEmailOrTelefoneWhatsapp(request.identificador(), request.identificador())
                .orElseThrow(UsuarioNotFoundException::new);
        return toAuthResponse(usuario);
    }

    @Transactional
    public void solicitarAlteracaoSenha(SolicitarAlteracaoSenhaRequest request) {
        String email = normalizarEmail(request.email());
        usuarioRepository.findByEmail(email)
                .filter(usuario -> Boolean.TRUE.equals(usuario.getAtivo()))
                .ifPresent(usuario -> codigoVerificacaoService.gerarEEnviar(email, TipoCodigoVerificacao.ALTERACAO_SENHA));
    }

    @Transactional
    public void confirmarAlteracaoSenha(ConfirmarAlteracaoSenhaRequest request) {
        String email = normalizarEmail(request.email());
        codigoVerificacaoService.validar(email, TipoCodigoVerificacao.ALTERACAO_SENHA, request.codigo());
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(UsuarioNotFoundException::new);
        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));
    }

    private AuthResponse toAuthResponse(Usuario usuario) {
        return toAuthResponse(usuario, jwtService.gerarToken(userDetailsService.loadUserByUsername(usuario.getEmail())));
    }

    private AuthResponse toAuthResponse(Usuario usuario, String token) {
        return new AuthResponse(
                usuario.getId(),
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                usuario.getTelefoneWhatsapp(),
                usuario.getRole(),
                token
        );
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }
}
