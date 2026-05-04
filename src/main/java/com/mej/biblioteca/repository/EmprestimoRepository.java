package com.mej.biblioteca.repository;

import com.mej.biblioteca.model.Emprestimo;
import com.mej.biblioteca.model.StatusEmprestimo;
import com.mej.biblioteca.model.Usuario;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    boolean existsByLeitorAndStatusIn(Usuario leitor, Collection<StatusEmprestimo> status);

    boolean existsByLivroIdAndStatusIn(Long livroId, Collection<StatusEmprestimo> status);

    List<Emprestimo> findByLeitorOrderByDataPedidoDesc(Usuario leitor);

    List<Emprestimo> findByStatus(StatusEmprestimo status);
}
