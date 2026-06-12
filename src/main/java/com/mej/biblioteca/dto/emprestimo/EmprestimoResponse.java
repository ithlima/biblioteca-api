package com.mej.biblioteca.dto.emprestimo;

import com.mej.biblioteca.model.entity.Emprestimo;
import com.mej.biblioteca.model.enums.StatusEmprestimo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record EmprestimoResponse(
        UUID id,
        UUID livroId,
        String nomeObra,
        UUID leitorId,
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
