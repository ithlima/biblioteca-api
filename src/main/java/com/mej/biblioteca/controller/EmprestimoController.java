package com.mej.biblioteca.controller;

import com.mej.biblioteca.dto.emprestimo.EmprestimoResponse;
import com.mej.biblioteca.dto.emprestimo.EmprestimoSolicitarRequest;
import com.mej.biblioteca.service.EmprestimoService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springdoc.core.annotations.ParameterObject;
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
    public EmprestimoResponse emprestar(@PathVariable UUID id) {
        return emprestimoService.emprestar(id);
    }

    @PatchMapping("/{id}/renovar")
    public EmprestimoResponse renovar(@PathVariable UUID id, Authentication authentication) {
        return emprestimoService.renovar(id, authentication);
    }

    @PatchMapping("/{id}/devolver")
    public EmprestimoResponse devolver(@PathVariable UUID id, Authentication authentication) {
        return emprestimoService.devolver(id, authentication);
    }

    @GetMapping("/meus")
    public Page<EmprestimoResponse> meus(@ParameterObject @PageableDefault(size = 10, sort = "dataPedido", direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication) {
        return emprestimoService.meus(pageable, authentication);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<EmprestimoResponse> listar(@ParameterObject @PageableDefault(size = 10, sort = "dataPedido", direction = Sort.Direction.DESC) Pageable pageable) {
        return emprestimoService.listar(pageable);
    }
}
