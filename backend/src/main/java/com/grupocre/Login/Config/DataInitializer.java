package com.grupocre.Login.Config;
import com.grupocre.Login.Models.Role;
import com.grupocre.Login.Models.User;
import com.grupocre.Login.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Busca si el usuario 'admin' ya existe
        User admin = userRepository.findByUsername("admin").orElse(null);

        if (admin == null) {
            // Si no existe, lo crea desde cero
            admin = new User("admin", "admin@grupocre.com", passwordEncoder.encode("admin123"), Role.ADMIN);
            userRepository.save(admin);
            System.out.println("✅ Usuario ADMIN creado exitosamente.");
        } else {
            // Si ya existe, actualiza su contraseña con el encriptador oficial y reinicia bloqueos
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setIntentosFallidos(0);
            admin.setBloqueado(false);
            userRepository.save(admin);
            System.out.println("✅ Contraseña del usuario ADMIN restablecida a 'admin123'.");
        }
    }
}