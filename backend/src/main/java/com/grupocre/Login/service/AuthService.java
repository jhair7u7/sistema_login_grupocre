package com.grupocre.Login.service;


import com.grupocre.Login.dto.*;
import com.grupocre.Login.entity.Rol;
import com.grupocre.Login.entity.Usuario;
import com.grupocre.Login.exception.CustomException;
import com.grupocre.Login.repository.RolRepository;
import com.grupocre.Login.repository.UsuarioRepository;
import com.grupocre.Login.security.JwtProvider;
import com.grupocre.Login.security.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final TokenBlacklistService tokenBlacklistService;

    @Value("${app.security.max-login-attempts}")
    private int maxAttempts;

    @Value("${app.security.lock-duration-minutes}")
    private int lockDurationMinutes;

    public AuthResponse register(RegisterRequest dto) {
        if (usuarioRepository.findByUsernameOrEmail(dto.getUsername(), dto.getUsername()).isPresent()) {
            throw new CustomException("El username ya está en uso");
        }
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new CustomException("El email ya está registrado");
        }

        Set<Rol> roles = new HashSet<>();
        roles.add(rolRepository.findByNombre("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Rol por defecto no encontrado")));
        if (dto.isAdmin()) {
            roles.add(rolRepository.findByNombre("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado")));
        }

        Usuario usuario = Usuario.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .activo(true)
                .bloqueado(false)
                .intentosFallidos(0)
                .roles(roles)
                .build();

        usuario = usuarioRepository.save(usuario);
        String token = jwtProvider.generateToken(usuario.getId(), usuario.getUsername(),
                usuario.getEmail(), mapRolesToList(usuario.getRoles()));

        return buildAuthResponse(usuario, token);
    }

    public AuthResponse login(LoginRequest dto) {
        Usuario usuario = usuarioRepository.findByUsernameOrEmail(dto.getLogin(), dto.getLogin())
                .orElseThrow(() -> new CustomException("Credenciales inválidas"));

        if (!usuario.getActivo()) {
            throw new CustomException("Cuenta deshabilitada");
        }

        if (usuario.getBloqueado()) {
            if (usuario.getFechaBloqueo() != null) {
                LocalDateTime unlockTime = usuario.getFechaBloqueo().plusMinutes(lockDurationMinutes);
                if (LocalDateTime.now().isBefore(unlockTime)) {
                    throw new CustomException("Cuenta bloqueada temporalmente. Intente más tarde.");
                } else {
                    // Desbloquear automáticamente
                    usuario.setBloqueado(false);
                    usuario.setIntentosFallidos(0);
                    usuario.setFechaBloqueo(null);
                    usuarioRepository.save(usuario);
                }
            } else {
                throw new CustomException("Cuenta bloqueada");
            }
        }

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            int newAttempts = usuario.getIntentosFallidos() + 1;
            usuario.setIntentosFallidos(newAttempts);
            if (newAttempts >= maxAttempts) {
                usuario.setBloqueado(true);
                usuario.setFechaBloqueo(LocalDateTime.now());
            }
            usuarioRepository.save(usuario);
            throw new CustomException("Credenciales inválidas");
        }

        // Login exitoso: resetear intentos
        usuario.setIntentosFallidos(0);
        usuario.setBloqueado(false);
        usuario.setFechaBloqueo(null);
        usuarioRepository.save(usuario);

        String token = jwtProvider.generateToken(usuario.getId(), usuario.getUsername(),
                usuario.getEmail(), mapRolesToList(usuario.getRoles()));

        return buildAuthResponse(usuario, token);
    }

    public void logout(String token) {
        if (token != null && jwtProvider.validateToken(token)) {
            Date expiration = jwtProvider.getExpirationDate(token);
            tokenBlacklistService.invalidate(token, expiration);
        }
    }

    public void forgotPassword(ForgotPasswordRequest dto) {
        Optional<Usuario> optionalUser = usuarioRepository.findByEmail(dto.getEmail());
        if (optionalUser.isEmpty()) {
            // No revelar si el email existe
            return;
        }
        Usuario usuario = optionalUser.get();
        String token = UUID.randomUUID().toString();
        usuario.setTokenRecuperacion(token);
        usuario.setFechaExpiracionToken(LocalDateTime.now().plusMinutes(15));
        usuarioRepository.save(usuario);

        // Enviar email con token (simulado)
        // emailService.sendRecoveryEmail(usuario.getEmail(), token);
    }

    public void resetPassword(ResetPasswordRequest dto) {
        Usuario usuario = usuarioRepository
                .findByTokenRecuperacionAndFechaExpiracionTokenAfter(dto.getToken(), LocalDateTime.now())
                .orElseThrow(() -> new CustomException("Token inválido o expirado"));

        usuario.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        usuario.setTokenRecuperacion(null);
        usuario.setFechaExpiracionToken(null);
        usuarioRepository.save(usuario);
    }

    private List<String> mapRolesToList(Set<Rol> roles) {
        return roles.stream().map(Rol::getNombre).collect(Collectors.toList());
    }

    private AuthResponse buildAuthResponse(Usuario usuario, String token) {
        return new AuthResponse(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                mapRolesToList(usuario.getRoles())
        );
    }
}