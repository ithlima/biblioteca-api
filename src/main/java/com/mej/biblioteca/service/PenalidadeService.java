package com.mej.biblioteca.service;

import com.mej.biblioteca.dto.PenalidadeResponse;
import com.mej.biblioteca.model.Usuario;
import com.mej.biblioteca.repository.PenalidadeRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PenalidadeService {

    private final PenalidadeRepository penalidadeRepository;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<PenalidadeResponse> minhas(Authentication authentication) {
        Usuario usuario = usuarioService.usuarioAutenticado(authentication);
        return penalidadeRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(PenalidadeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PenalidadeResponse> listarTodas() {
        return penalidadeRepository.findAll()
                .stream()
                .map(PenalidadeResponse::from)
                .toList();
    }

    @Transactional
    public void encerrarPenalidadesVencidas() {
        LocalDate hoje = LocalDate.now();
        penalidadeRepository.findByAtivaTrue()
                .stream()
                .filter(penalidade -> penalidade.getDataFim() != null && penalidade.getDataFim().isBefore(hoje))
                .forEach(penalidade -> penalidade.setAtiva(false));
    }
}
