package com.grupocre.Login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.grupocre.Login.entity.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    Optional<PasswordResetToken> findByToken(String token);
}
