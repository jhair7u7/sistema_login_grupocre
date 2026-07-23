package com.grupocre.Login.Service;

import com.grupocre.Login.Dto.JwtResponse;
import com.grupocre.Login.Dto.LoginRequest;
import com.grupocre.Login.Models.User;
import com.grupocre.Login.Repository.UserRepository;
import com.grupocre.Login.Security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public JwtResponse authenticateUser(LoginRequest request) {
        User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.isBloqueado()) {
            throw new RuntimeException("La cuenta está bloqueada por demasiados intentos fallidos.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            int intentos = user.getIntentosFallidos() + 1;
            user.setIntentosFallidos(intentos);
            if (intentos >= 3) {
                user.setBloqueado(true);
            }
            userRepository.save(user);
            throw new RuntimeException("Contraseña incorrecta. Intentos restantes: " + (3 - intentos));
        }

        // Si la clave es correcta, reiniciamos el contador de intentos
        user.setIntentosFallidos(0);
        userRepository.save(user);

        String token = jwtUtils.generateToken(user.getUsername());
        return new JwtResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());
    }

    public String generateResetPasswordToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No existe usuario registrado con ese email"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiracion(LocalDateTime.now().plusHours(1)); // Válido por 1 hora
        userRepository.save(user);

        return token;
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Token de recuperación inválido"));

        if (user.getResetTokenExpiracion().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El token de recuperación ha expirado");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiracion(null);
        user.setBloqueado(false); // Desbloquea la cuenta al cambiar contraseña
        user.setIntentosFallidos(0);
        userRepository.save(user);
    }
}