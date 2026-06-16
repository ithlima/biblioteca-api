package com.mej.biblioteca.dto.categoria;

import com.mej.biblioteca.model.entity.Categoria;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record CategoriaResponse(
        UUID id,
        String nome,
        String descricao
) {
    public static CategoriaResponse from(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao()
        );
    }

    public static List<CategoriaResponse> from(Set<Categoria> categorias) {
        return categorias.stream()
                .map(CategoriaResponse::from)
                .sorted(Comparator.comparing(CategoriaResponse::nome, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }
}
