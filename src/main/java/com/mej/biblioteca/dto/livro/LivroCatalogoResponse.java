package com.mej.biblioteca.dto.livro;

import com.mej.biblioteca.dto.categoria.CategoriaResponse;
import com.mej.biblioteca.model.entity.Livro;
import java.util.List;
import java.util.UUID;

public record LivroCatalogoResponse(
        UUID id,
        String nomeObra,
        String autor,
        String editora,
        String volume,
        String descricao,
        List<CategoriaResponse> categorias,
        Integer quantidade,
        Integer quantidadeEmprestada,
        String fotoCapaUrl
) {
    public static LivroCatalogoResponse from(Livro livro, Integer quantidadeEmprestada) {
        return new LivroCatalogoResponse(
                livro.getId(),
                livro.getNomeObra(),
                livro.getAutor(),
                livro.getEditora(),
                livro.getVolume(),
                livro.getDescricao(),
                CategoriaResponse.from(livro.getCategorias()),
                livro.getQuantidade(),
                quantidadeEmprestada,
                livro.getFotoCapaUrl()
        );
    }
}
