package com.mej.biblioteca.controller;

import com.mej.biblioteca.dto.AuthCadastroRequest;
import com.mej.biblioteca.dto.AuthResponse;
import com.mej.biblioteca.dto.LoginRequest;
import com.mej.biblioteca.service.AuthService;
import jakarta.validation.Valid;
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

    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse cadastrar(@RequestBody @Valid AuthCadastroRequest request) {
        return authService.cadastrar(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }
}
