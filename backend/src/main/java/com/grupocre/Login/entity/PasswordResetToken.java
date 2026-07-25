package com.grupocre.Login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tokens_recuperacion")
@Data
@NoArgsConstructor @AllArgsConstructor @Builder
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 255)
    private String token;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime expirationDate;

    @Column(name = "usado", nullable = false)     // ⬅️ Mapeo explícito a "usado"
    private boolean used = false;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}