package com.mej.biblioteca.dto;

import com.mej.biblioteca.model.Emprestimo;
import com.mej.biblioteca.model.StatusEmprestimo;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmprestimoResponse(
        Long id,
        Long livroId,
        String nomeObra,
        Long leitorId,
        String nomeLeitor,
        LocalDateTime dataPedido,
        LocalDate dataEmprestimo,
        LocalDate dataDevolucaoPrevista,
        LocalDate dataDevolucaoReal,
        Integer quantidadeRenovacoes,
        StatusEmprestimo status
) {
    public static EmprestimoResponse from(Emprestimo emprestimo) {
        return new EmprestimoResponse(
                emprestimo.getId(),
                emprestimo.getLivro().getId(),
                emprestimo.getLivro().getNomeObra(),
                emprestimo.getLeitor().getId(),
                emprestimo.getLeitor().getNomeCompleto(),
                emprestimo.getDataPedido(),
                emprestimo.getDataEmprestimo(),
                emprestimo.getDataDevolucaoPrevista(),
                emprestimo.getDataDevolucaoReal(),
                emprestimo.getQuantidadeRenovacoes(),
                emprestimo.getStatus()
        );
    }
}
