package com.mej.biblioteca.controller;

import com.mej.biblioteca.dto.AuthCadastroRequest;
import com.mej.biblioteca.dto.AuthResponse;
import com.mej.biblioteca.dto.ConfirmarAlteracaoSenhaRequest;
import com.mej.biblioteca.dto.ConfirmarCadastroRequest;
import com.mej.biblioteca.dto.LoginRequest;
import com.mej.biblioteca.dto.ReenviarCodigoRequest;
import com.mej.biblioteca.dto.SolicitarAlteracaoSenhaRequest;
import com.mej.biblioteca.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private static final String MENSAGEM_KEY = "mensagem";

    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse cadastrar(@RequestBody @Valid AuthCadastroRequest request) {
        return authService.cadastrar(request);
    }

    @PostMapping("/cadastro/confirmar")
    public AuthResponse confirmarCadastro(@RequestBody @Valid ConfirmarCadastroRequest request) {
        return authService.confirmarCadastro(request);
    }

    @PostMapping("/reenviar-codigo")
    @Operation(summary = "Reenviar código de verificação do cadastro")
    public Map<String, String> reenviarCodigo(@RequestBody @Valid ReenviarCodigoRequest request) {
        authService.reenviarCodigo(request);
        return Map.of(MENSAGEM_KEY, "Código de verificação reenviado.");
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/senha/solicitar-alteracao")
    public Map<String, String> solicitarAlteracaoSenha(@RequestBody @Valid SolicitarAlteracaoSenhaRequest request) {
        authService.solicitarAlteracaoSenha(request);
        return Map.of(MENSAGEM_KEY, "Se o e-mail estiver cadastrado e ativo, um codigo sera enviado.");
    }

    @PostMapping("/senha/confirmar-alteracao")
    public Map<String, String> confirmarAlteracaoSenha(@RequestBody @Valid ConfirmarAlteracaoSenhaRequest request) {
        authService.confirmarAlteracaoSenha(request);
        return Map.of(MENSAGEM_KEY, "Senha alterada com sucesso.");
    }
}
