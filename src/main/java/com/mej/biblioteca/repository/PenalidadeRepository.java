package com.mej.biblioteca.repository;

import com.mej.biblioteca.model.Penalidade;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PenalidadeRepository extends JpaRepository<Penalidade, UUID> {

    boolean existsByEmprestimoIdAndAtivaTrue(UUID emprestimoId);

    boolean existsByUsuarioIdAndAtivaTrue(UUID usuarioId);

    List<Penalidade> findByUsuarioId(UUID usuarioId);

    List<Penalidade> findByAtivaTrue();
}
