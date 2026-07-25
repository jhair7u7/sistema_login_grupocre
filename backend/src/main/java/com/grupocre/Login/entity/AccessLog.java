package com.grupocre.Login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.grupocre.Login.entity.enums.EventType;

import java.time.LocalDateTime;

@Entity
@Table(name = "log_accesos")
@Data
@NoArgsConstructor @AllArgsConstructor @Builder
public class AccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false)
    private EventType eventType;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "exitoso", nullable = false)  // Asegúrate de que coincida con "exitoso"
    private boolean successful;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime eventDate = LocalDateTime.now();
}