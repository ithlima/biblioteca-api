package com.mej.biblioteca.dto.livro;

import com.mej.biblioteca.dto.categoria.CategoriaResponse;
import com.mej.biblioteca.model.entity.Livro;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record LivroResponse(
        UUID id,
        String nomeObra,
        String autor,
        String editora,
        String volume,
        String descricao,
        List<CategoriaResponse> categorias,
        Integer quantidade,
        Integer quantidadeEmprestada,
        String fotoCapaUrl,
        Boolean oculto,
        String motivoOcultacao,
        UUID criadoPorId,
        UUID editadoPorId,
        LocalDateTime criadoEm,
        LocalDateTime editadoEm
) {
    public static LivroResponse from(Livro livro, Integer quantidadeEmprestada) {
        return new LivroResponse(
                livro.getId(),
                livro.getNomeObra(),
                livro.getAutor(),
                livro.getEditora(),
                livro.getVolume(),
                livro.getDescricao(),
                CategoriaResponse.from(livro.getCategorias()),
                livro.getQuantidade(),
                quantidadeEmprestada,
                livro.getFotoCapaUrl(),
                livro.getOculto(),
                livro.getMotivoOcultacao(),
                livro.getCriadoPor() == null ? null : livro.getCriadoPor().getId(),
                livro.getEditadoPor() == null ? null : livro.getEditadoPor().getId(),
                livro.getCriadoEm(),
                livro.getEditadoEm()
        );
    }
}
