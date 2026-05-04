package com.mej.biblioteca.controller;

import com.mej.biblioteca.dto.UsuarioResponse;
import com.mej.biblioteca.service.UsuarioService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @PatchMapping("/{id}/promover-admin")
    public UsuarioResponse promoverAdmin(@PathVariable Long id) {
        return usuarioService.promoverAdmin(id);
    }

    @PatchMapping("/{id}/rebaixar-leitor")
    public UsuarioResponse rebaixarLeitor(@PathVariable Long id) {
        return usuarioService.rebaixarLeitor(id);
    }

    @PatchMapping("/{id}/bloquear")
    public UsuarioResponse bloquear(@PathVariable Long id) {
        return usuarioService.bloquear(id);
    }
}
