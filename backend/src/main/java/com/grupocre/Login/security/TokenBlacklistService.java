package com.grupocre.Login.security;


import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    private final Map<String, Date> blacklist = new ConcurrentHashMap<>();

    public void invalidate(String token, Date expiration) {
        blacklist.put(token, expiration);
    }

    public boolean isBlacklisted(String token) {
        return blacklist.containsKey(token);
    }

    // Limpiar periódicamente tokens expirados
    public void cleanExpired() {
        Date now = new Date();
        blacklist.entrySet().removeIf(entry -> entry.getValue().before(now));
    }
}