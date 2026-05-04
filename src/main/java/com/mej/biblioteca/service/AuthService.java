package com.mej.biblioteca.service;

import com.mej.biblioteca.dto.AuthCadastroRequest;
import com.mej.biblioteca.dto.AuthResponse;
import com.mej.biblioteca.dto.LoginRequest;
import com.mej.biblioteca.exception.BusinessException;
import com.mej.biblioteca.model.Role;
import com.mej.biblioteca.model.Usuario;
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

    @Transactional
    public AuthResponse cadastrar(AuthCadastroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessException("Ja existe usuario cadastrado com este e-mail.");
        }
        if (request.telefoneWhatsapp() != null
                && !request.telefoneWhatsapp().isBlank()
                && usuarioRepository.existsByTelefoneWhatsapp(request.telefoneWhatsapp())) {
            throw new BusinessException("Ja existe usuario cadastrado com este telefone.");
        }

        Usuario usuario = Usuario.builder()
                .nomeCompleto(request.nomeCompleto())
                .email(request.email())
                .telefoneWhatsapp(request.telefoneWhatsapp())
                .senha(passwordEncoder.encode(request.senha()))
                .role(Role.LEITOR)
                .ativo(true)
                .loginBloqueado(false)
                .build();

        Usuario salvo = usuarioRepository.save(usuario);
        return toAuthResponse(salvo);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.identificador(), request.senha())
        );

        Usuario usuario = usuarioRepository.findByEmailOrTelefoneWhatsapp(request.identificador(), request.identificador())
                .orElseThrow(() -> new BusinessException("Usuario nao encontrado."));
        return toAuthResponse(usuario);
    }

    private AuthResponse toAuthResponse(Usuario usuario) {
        String token = jwtService.gerarToken(userDetailsService.loadUserByUsername(usuario.getEmail()));
        return new AuthResponse(
                usuario.getId(),
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                usuario.getTelefoneWhatsapp(),
                usuario.getRole(),
                token
        );
    }
}
