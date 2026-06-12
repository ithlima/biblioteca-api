package com.mej.biblioteca.controller;

import com.mej.biblioteca.dto.penalidade.PenalidadeResponse;
import com.mej.biblioteca.service.EmprestimoService;
import com.mej.biblioteca.service.PenalidadeService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/penalidades")
@RequiredArgsConstructor
public class PenalidadeController {

    private final PenalidadeService penalidadeService;
    private final EmprestimoService emprestimoService;

    @GetMapping("/minhas")
    public List<PenalidadeResponse> minhas(Authentication authentication) {
        return penalidadeService.minhas(authentication);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PenalidadeResponse> listarTodas() {
        return penalidadeService.listarTodas();
    }

    @PostMapping("/verificar-atrasos")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> verificarAtrasos() {
        emprestimoService.verificarAtrasosAutomaticamente();
        penalidadeService.encerrarPenalidadesVencidas();
        return Map.of("mensagem", "Verificacao de atrasos e penalidades vencidas realizada com sucesso.");
    }
}
