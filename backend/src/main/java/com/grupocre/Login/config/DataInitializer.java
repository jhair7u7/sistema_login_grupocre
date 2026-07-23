package com.grupocre.Login.config;


import com.grupocre.Login.entity.Rol;
import com.grupocre.Login.repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;

    public DataInitializer(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    public void run(String... args) {
        if (rolRepository.findByNombre("ROLE_USER").isEmpty()) {
            rolRepository.save(new Rol(null, "ROLE_USER", "Usuario estándar"));
        }
        if (rolRepository.findByNombre("ROLE_ADMIN").isEmpty()) {
            rolRepository.save(new Rol(null, "ROLE_ADMIN", "Administrador del sistema"));
        }
    }
}