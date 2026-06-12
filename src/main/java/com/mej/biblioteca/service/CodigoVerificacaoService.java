package com.mej.biblioteca.service;

import com.mej.biblioteca.exception.domain.CodigoVerificacaoInvalidoException;
import com.mej.biblioteca.model.entity.CodigoVerificacao;
import com.mej.biblioteca.model.enums.TipoCodigoVerificacao;
import com.mej.biblioteca.repository.CodigoVerificacaoRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CodigoVerificacaoService {

    private static final int VALIDADE_MINUTOS = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final CodigoVerificacaoRepository codigoVerificacaoRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public void gerarEEnviar(String email, TipoCodigoVerificacao tipo) {
        String codigo = gerarCodigo();
        CodigoVerificacao codigoVerificacao = CodigoVerificacao.builder()
                .email(normalizarEmail(email))
                .codigoHash(passwordEncoder.encode(codigo))
                .tipo(tipo)
                .expiraEm(LocalDateTime.now(java.time.ZoneId.of("UTC")).plusMinutes(VALIDADE_MINUTOS))
                .build();
        codigoVerificacaoRepository.save(codigoVerificacao);
        emailService.enviarCodigoVerificacao(email, assunto(tipo), codigo);
    }

    @Transactional
    public void validar(String email, TipoCodigoVerificacao tipo, String codigo) {
        CodigoVerificacao codigoVerificacao = codigoVerificacaoRepository
                .findFirstByEmailAndTipoAndUsadoEmIsNullOrderByCriadoEmDesc(normalizarEmail(email), tipo)
                .orElseThrow(CodigoVerificacaoInvalidoException::new);

        if (codigoVerificacao.getExpiraEm().isBefore(LocalDateTime.now(java.time.ZoneId.of("UTC")))) {
            throw new CodigoVerificacaoInvalidoException();
        }
        if (!passwordEncoder.matches(codigo, codigoVerificacao.getCodigoHash())) {
            throw new CodigoVerificacaoInvalidoException();
        }

        codigoVerificacao.setUsadoEm(LocalDateTime.now(java.time.ZoneId.of("UTC")));
    }

    private String gerarCodigo() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    private String assunto(TipoCodigoVerificacao tipo) {
        return switch (tipo) {
            case CADASTRO -> "Codigo de validacao do cadastro";
            case ALTERACAO_SENHA -> "Codigo para alteracao de senha";
        };
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }
}
