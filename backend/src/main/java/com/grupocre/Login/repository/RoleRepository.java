package com.grupocre.Login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.grupocre.Login.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByNombre(String nombre);
}