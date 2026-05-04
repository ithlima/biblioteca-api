package com.mej.biblioteca.dto;

import com.mej.biblioteca.model.Livro;
import java.time.LocalDateTime;
import java.util.UUID;

public record LivroResponse(
        UUID id,
        String nomeObra,
        String autor,
        String editora,
        String volume,
        String descricao,
        String categorias,
        Integer quantidade,
        String fotoCapaUrl,
        Boolean oculto,
        String motivoOcultacao,
        UUID criadoPorId,
        UUID editadoPorId,
        LocalDateTime criadoEm,
        LocalDateTime editadoEm
) {
    public static LivroResponse from(Livro livro) {
        return new LivroResponse(
                livro.getId(),
                livro.getNomeObra(),
                livro.getAutor(),
                livro.getEditora(),
                livro.getVolume(),
                livro.getDescricao(),
                livro.getCategorias(),
                livro.getQuantidade(),
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
