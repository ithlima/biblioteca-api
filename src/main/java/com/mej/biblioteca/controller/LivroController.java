package com.mej.biblioteca.controller;

import com.mej.biblioteca.dto.LivroOcultarRequest;
import com.mej.biblioteca.dto.LivroRequest;
import com.mej.biblioteca.dto.LivroResponse;
import com.mej.biblioteca.service.LivroService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor
public class LivroController {

    private final LivroService livroService;

    @GetMapping
    public Page<?> listar(@PageableDefault(size = 10) Pageable pageable, Authentication authentication) {
        return livroService.listar(pageable, authentication);
    }

    @GetMapping("/{id}")
    public Object buscar(@PathVariable UUID id, Authentication authentication) {
        return livroService.buscar(id, authentication);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LivroResponse criar(@RequestBody @Valid LivroRequest request, Authentication authentication) {
        return livroService.criar(request, authentication);
    }

    @PutMapping("/{id}")
    public LivroResponse atualizar(@PathVariable UUID id, @RequestBody @Valid LivroRequest request, Authentication authentication) {
        return livroService.atualizar(id, request, authentication);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID id) {
        livroService.remover(id);
    }

    @PatchMapping("/{id}/ocultar")
    public LivroResponse ocultar(@PathVariable UUID id, @RequestBody @Valid LivroOcultarRequest request, Authentication authentication) {
        return livroService.ocultar(id, request, authentication);
    }

    @PatchMapping("/{id}/disponibilizar")
    public LivroResponse disponibilizar(@PathVariable UUID id, Authentication authentication) {
        return livroService.disponibilizar(id, authentication);
    }
}
