package com.mej.biblioteca.repository;

import com.mej.biblioteca.model.Penalidade;
import com.mej.biblioteca.model.Usuario;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PenalidadeRepository extends JpaRepository<Penalidade, Long> {

    boolean existsByEmprestimoIdAndAtivaTrue(Long emprestimoId);

    boolean existsByUsuarioIdAndAtivaTrue(Long usuarioId);

    List<Penalidade> findByUsuarioId(Long usuarioId);

    List<Penalidade> findByAtivaTrue();

    boolean existsByUsuarioAndAtivaTrueAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
            Usuario usuario,
            LocalDate inicio,
            LocalDate fim
    );

    boolean existsByUsuarioAndAtivaTrueAndDataFimIsNull(Usuario usuario);
}
