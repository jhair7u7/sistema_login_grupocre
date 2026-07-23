package com.grupocre.Login.config;


import com.grupocre.Login.security.TokenBlacklistService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    private final TokenBlacklistService blacklistService;

    public SchedulingConfig(TokenBlacklistService blacklistService) {
        this.blacklistService = blacklistService;
    }

    @Scheduled(fixedRate = 600000) // cada 10 minutos
    public void cleanBlacklist() {
        blacklistService.cleanExpired();
    }
}