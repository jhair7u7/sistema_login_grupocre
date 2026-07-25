package com.grupocre.Login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.grupocre.Login.entity.AccessLog;

public interface AccessLogRepository extends JpaRepository<AccessLog, Integer> {
}
