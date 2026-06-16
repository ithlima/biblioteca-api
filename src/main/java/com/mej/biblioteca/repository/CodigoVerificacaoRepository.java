package com.mej.biblioteca.repository;

import com.mej.biblioteca.model.entity.CodigoVerificacao;
import com.mej.biblioteca.model.enums.TipoCodigoVerificacao;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodigoVerificacaoRepository extends JpaRepository<CodigoVerificacao, UUID> {

    Optional<CodigoVerificacao> findFirstByEmailAndTipoAndUsadoEmIsNullOrderByCriadoEmDesc(
            String email,
            TipoCodigoVerificacao tipo
    );
}
