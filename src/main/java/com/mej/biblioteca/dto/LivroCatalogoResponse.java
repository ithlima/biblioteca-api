package com.mej.biblioteca.dto;

import com.mej.biblioteca.model.Livro;
import java.util.UUID;

public record LivroCatalogoResponse(
        UUID id,
        String nomeObra,
        String autor,
        String editora,
        String volume,
        String descricao,
        String categorias,
        Integer quantidade,
        String fotoCapaUrl
) {
    public static LivroCatalogoResponse from(Livro livro) {
        return new LivroCatalogoResponse(
                livro.getId(),
                livro.getNomeObra(),
                livro.getAutor(),
                livro.getEditora(),
                livro.getVolume(),
                livro.getDescricao(),
                livro.getCategorias(),
                livro.getQuantidade(),
                livro.getFotoCapaUrl()
        );
    }
}
