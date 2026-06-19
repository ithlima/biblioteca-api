package com.mej.biblioteca.model.entity;

import com.mej.biblioteca.model.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String nomeCompleto;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 20)
    private String telefoneWhatsapp;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private Boolean ativo;

    @Column(nullable = false)
    private Boolean loginBloqueado;

    @Column(nullable = false)
    private Boolean emailValidado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(length = 500)
    private String motivoBloqueio;

    @PrePersist
    void prePersist() {
        if (role == null) {
            role = Role.LEITOR;
        }
        if (ativo == null) {
            ativo = true;
        }
        if (loginBloqueado == null) {
            loginBloqueado = false;
        }
        if (emailValidado == null) {
            emailValidado = false;
        }
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now(java.time.ZoneId.of("UTC"));
        }
    }
}
