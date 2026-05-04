package com.mej.biblioteca.dto;

import com.mej.biblioteca.model.Penalidade;
import java.time.LocalDate;

public record PenalidadeResponse(
        Long id,
        Long usuarioId,
        String nomeUsuario,
        Long emprestimoId,
        String motivo,
        LocalDate dataInicio,
        LocalDate dataFim,
        Boolean ativa
) {
    public static PenalidadeResponse from(Penalidade penalidade) {
        return new PenalidadeResponse(
                penalidade.getId(),
                penalidade.getUsuario().getId(),
                penalidade.getUsuario().getNomeCompleto(),
                penalidade.getEmprestimo() == null ? null : penalidade.getEmprestimo().getId(),
                penalidade.getMotivo(),
                penalidade.getDataInicio(),
                penalidade.getDataFim(),
                penalidade.getAtiva()
        );
    }
}
