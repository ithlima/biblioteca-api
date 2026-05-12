package com.mej.biblioteca.repository;

import com.mej.biblioteca.model.Emprestimo;
import com.mej.biblioteca.model.StatusEmprestimo;
import com.mej.biblioteca.model.Usuario;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, UUID> {

    boolean existsByLeitorAndStatusIn(Usuario leitor, Collection<StatusEmprestimo> status);

    boolean existsByLivroId(UUID livroId);

    boolean existsByLivroIdAndStatusIn(UUID livroId, Collection<StatusEmprestimo> status);

    List<Emprestimo> findByLeitorOrderByDataPedidoDesc(Usuario leitor);

    List<Emprestimo> findByStatus(StatusEmprestimo status);
}
