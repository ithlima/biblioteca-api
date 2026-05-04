package com.mej.biblioteca.repository;

import com.mej.biblioteca.model.Livro;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LivroRepository extends JpaRepository<Livro, UUID> {

    List<Livro> findByOcultoFalse();

    @Query("""
            select count(l) > 0
            from Livro l
            where lower(trim(l.nomeObra)) = lower(trim(:nomeObra))
              and lower(trim(l.autor)) = lower(trim(:autor))
              and lower(trim(coalesce(l.editora, ''))) = lower(trim(coalesce(:editora, '')))
              and lower(trim(coalesce(l.volume, ''))) = lower(trim(coalesce(:volume, '')))
            """)
    boolean existsDuplicado(
            @Param("nomeObra") String nomeObra,
            @Param("autor") String autor,
            @Param("editora") String editora,
            @Param("volume") String volume
    );

    @Query("""
            select count(l) > 0
            from Livro l
            where l.id <> :id
              and lower(trim(l.nomeObra)) = lower(trim(:nomeObra))
              and lower(trim(l.autor)) = lower(trim(:autor))
              and lower(trim(coalesce(l.editora, ''))) = lower(trim(coalesce(:editora, '')))
              and lower(trim(coalesce(l.volume, ''))) = lower(trim(coalesce(:volume, '')))
            """)
    boolean existsDuplicadoEmOutroLivro(
            @Param("nomeObra") String nomeObra,
            @Param("autor") String autor,
            @Param("editora") String editora,
            @Param("volume") String volume,
            @Param("id") UUID id
    );
}
