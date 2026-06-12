package com.mej.biblioteca.repository;

import com.mej.biblioteca.model.enums.Role;
import com.mej.biblioteca.model.entity.Usuario;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByEmailOrTelefoneWhatsapp(String email, String telefoneWhatsapp);

    boolean existsByEmail(String email);

    boolean existsByTelefoneWhatsapp(String telefoneWhatsapp);

    boolean existsByRole(Role role);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select u from Usuario u
            where u.role = :role and u.ativo = true and u.loginBloqueado = false
            order by u.id
            """)
    List<Usuario> findAllAtivosByRoleForUpdate(@Param("role") Role role);
}
