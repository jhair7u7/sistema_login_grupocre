package com.grupocre.Login.repository;


import com.grupocre.Login.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsernameOrEmail(String username, String email);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByTokenRecuperacionAndFechaExpiracionTokenAfter(
            String token, LocalDateTime now);
}