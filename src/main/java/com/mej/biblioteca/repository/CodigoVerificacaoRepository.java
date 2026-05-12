package com.mej.biblioteca.repository;

import com.mej.biblioteca.model.CodigoVerificacao;
import com.mej.biblioteca.model.TipoCodigoVerificacao;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodigoVerificacaoRepository extends JpaRepository<CodigoVerificacao, UUID> {

    Optional<CodigoVerificacao> findFirstByEmailAndTipoAndUsadoEmIsNullOrderByCriadoEmDesc(
            String email,
            TipoCodigoVerificacao tipo
    );
}
