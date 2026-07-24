package com.grupocre.Login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.grupocre.Login.entity.SessionToken;
import com.grupocre.Login.entity.User;

import java.util.List;
import java.util.Optional;

public interface SessionTokenRepository extends JpaRepository<SessionToken, Integer> {
    Optional<SessionToken> findByToken(String token);
    List<SessionToken> findByUserAndRevokedFalse(User user);
}
