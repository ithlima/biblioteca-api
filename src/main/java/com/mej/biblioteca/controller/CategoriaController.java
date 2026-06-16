package com.mej.biblioteca.controller;

import com.mej.biblioteca.dto.categoria.CategoriaRequest;
import com.mej.biblioteca.dto.categoria.CategoriaResponse;
import com.mej.biblioteca.service.CategoriaService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaResponse criar(@RequestBody @Valid CategoriaRequest request) {
        return categoriaService.criar(request);
    }

    @GetMapping
    public List<CategoriaResponse> listar() {
        return categoriaService.listar();
    }

    @GetMapping("/{id}")
    public CategoriaResponse buscar(@PathVariable UUID id) {
        return categoriaService.buscar(id);
    }

    @PutMapping("/{id}")
    public CategoriaResponse atualizar(@PathVariable UUID id, @RequestBody @Valid CategoriaRequest request) {
        return categoriaService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID id) {
        categoriaService.remover(id);
    }
}
