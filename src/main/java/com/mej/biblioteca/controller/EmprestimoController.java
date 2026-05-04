package com.mej.biblioteca.controller;

import com.mej.biblioteca.dto.EmprestimoResponse;
import com.mej.biblioteca.dto.EmprestimoSolicitarRequest;
import com.mej.biblioteca.service.EmprestimoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @PostMapping("/solicitar")
    @ResponseStatus(HttpStatus.CREATED)
    public EmprestimoResponse solicitar(@RequestBody @Valid EmprestimoSolicitarRequest request, Authentication authentication) {
        return emprestimoService.solicitar(request, authentication);
    }

    @PostMapping("/{id}/emprestar")
    @PreAuthorize("hasRole('ADMIN')")
    public EmprestimoResponse emprestar(@PathVariable Long id) {
        return emprestimoService.emprestar(id);
    }

    @PatchMapping("/{id}/renovar")
    public EmprestimoResponse renovar(@PathVariable Long id, Authentication authentication) {
        return emprestimoService.renovar(id, authentication);
    }

    @PatchMapping("/{id}/devolver")
    public EmprestimoResponse devolver(@PathVariable Long id, Authentication authentication) {
        return emprestimoService.devolver(id, authentication);
    }

    @GetMapping("/meus")
    public List<EmprestimoResponse> meus(Authentication authentication) {
        return emprestimoService.meus(authentication);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<EmprestimoResponse> listar() {
        return emprestimoService.listar();
    }
}
