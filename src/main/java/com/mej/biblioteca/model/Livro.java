package com.mej.biblioteca.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
@Table(name = "livros")
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 180)
    private String nomeObra;

    @Column(nullable = false, length = 120)
    private String autor;

    @Column(length = 120)
    private String editora;

    @Column(length = 50)
    private String volume;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(length = 255)
    private String categorias;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(length = 500)
    private String fotoCapaUrl;

    @Column(nullable = false)
    private Boolean oculto;

    @Column(length = 500)
    private String motivoOcultacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criado_por_id")
    private Usuario criadoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editado_por_id")
    private Usuario editadoPor;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    private LocalDateTime editadoEm;

    @PrePersist
    void prePersist() {
        if (quantidade == null) {
            quantidade = 0;
        }
        if (oculto == null) {
            oculto = false;
        }
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }

    @PreUpdate
    void preUpdate() {
        editadoEm = LocalDateTime.now();
    }
}
