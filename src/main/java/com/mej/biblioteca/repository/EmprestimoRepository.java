package com.mej.biblioteca.repository;

import com.mej.biblioteca.model.Emprestimo;
import com.mej.biblioteca.model.StatusEmprestimo;
import com.mej.biblioteca.model.Usuario;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, UUID> {

    boolean existsByLeitorAndStatusIn(Usuario leitor, Collection<StatusEmprestimo> status);

    boolean existsByLivroId(UUID livroId);

    boolean existsByLivroIdAndStatusIn(UUID livroId, Collection<StatusEmprestimo> status);

    @EntityGraph(attributePaths = {"livro", "leitor"})
    Page<Emprestimo> findByLeitorOrderByDataPedidoDesc(Usuario leitor, Pageable pageable);

    List<Emprestimo> findByStatus(StatusEmprestimo status);

    @EntityGraph(attributePaths = {"livro", "leitor"})
    Page<Emprestimo> findAll(Pageable pageable);
}
