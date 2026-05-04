package com.mej.biblioteca.repository;

import com.mej.biblioteca.model.Usuario;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByEmailOrTelefoneWhatsapp(String email, String telefoneWhatsapp);

    boolean existsByEmail(String email);

    boolean existsByTelefoneWhatsapp(String telefoneWhatsapp);
}
