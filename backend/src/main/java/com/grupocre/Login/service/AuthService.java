package com.grupocre.Login.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.grupocre.Login.config.JwtUtil;
import com.grupocre.Login.dto.*;
import com.grupocre.Login.entity.*;
import com.grupocre.Login.entity.enums.EventType;
import com.grupocre.Login.entity.enums.UserStatus;
import com.grupocre.Login.exception.AccountBlockedException;
import com.grupocre.Login.exception.BadCredentialsException;
import com.grupocre.Login.exception.CustomException;
import com.grupocre.Login.repository.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SessionTokenRepository sessionTokenRepository;

    @Autowired
    private AccessLogRepository accessLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RecoveryService recoveryService;

    @Value("${app.security.max-failed-attempts}")
    private int maxFailedAttempts;

    @Transactional
    public User register(RegisterRequest request, String ip) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomException("Correo electrónico ya registrado");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new CustomException("Nombre de usuario ya está en uso");
        }

        Role userRole = roleRepository.findByNombre("USUARIO")
                .orElseThrow(() -> new CustomException("USUARIO rol no encontrado en la base de datos"));

        User user = User.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .estado(UserStatus.ACTIVO)
                .build();

        user = userRepository.save(user);
        return user;
    }

    @Transactional(noRollbackFor = {
            com.grupocre.Login.exception.BadCredentialsException.class,
            com.grupocre.Login.exception.AccountBlockedException.class
    })
    public TokenResponse login(LoginRequest request, String ip) {
        String login = request.getLogin();
        Optional<User> userOpt = userRepository.findByEmailOrUsername(login);

        if (userOpt.isEmpty()) {
            logAccess(null, EventType.INTENTO_FALLIDO, ip, false);
            throw new BadCredentialsException("Credenciales inválidas");
        }

        User user = userOpt.get();

        if (user.getEstado() == UserStatus.BLOQUEADO) {
            logAccess(user, EventType.INTENTO_FALLIDO, ip, false);
            throw new AccountBlockedException("Cuenta bloqueada. Contacta al administrador.");
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login, request.getPassword())
            );

            // Reset failed attempts on success
            user.setFailedAttempts(0);
            user.setEstado(UserStatus.ACTIVO);
            user.setLockDate(null);
            userRepository.save(user);

            // Generate JWT and store session
            String jwt = jwtUtil.generateToken(user);
            SessionToken session = SessionToken.builder()
                    .user(user)
                    .token(jwt)
                    .expirationDate(LocalDateTime.now().plusHours(1))
                    .revoked(false)
                    .build();
            sessionTokenRepository.save(session);

            logAccess(user, EventType.LOGIN, ip, true);
            return new TokenResponse(jwt, "Inicio de sesión exitoso");

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            user.setFailedAttempts(user.getFailedAttempts() + 1);
            if (user.getFailedAttempts() >= maxFailedAttempts) {
                user.setEstado(UserStatus.BLOQUEADO);
                user.setLockDate(LocalDateTime.now());
                logAccess(user, EventType.BLOQUEO, ip, false);
            }
            userRepository.save(user);
            logAccess(user, EventType.INTENTO_FALLIDO, ip, false);
            throw new com.grupocre.Login.exception.BadCredentialsException("Credenciales inválidas");
        } catch (org.springframework.security.authentication.DisabledException e) {
            logger.warn("El usuario deshabilitado intentó iniciar sesión: {}", user.getEmail());
            logAccess(user, EventType.INTENTO_FALLIDO, ip, false);
            throw new com.grupocre.Login.exception.BadCredentialsException("Credenciales inválidas");
        } catch (org.springframework.security.authentication.LockedException e) {
            logger.warn("El usuario bloqueado trató de iniciar sesión: {}", user.getEmail());
            logAccess(user, EventType.INTENTO_FALLIDO, ip, false);
            throw new AccountBlockedException("Account locked. Contact the administrator.");
        } catch (Exception e) {
            logger.error("Error inesperado durante el inicio de sesión del usuario: {}", user.getEmail(), e);
            logAccess(user, EventType.INTENTO_FALLIDO, ip, false);
            throw new CustomException("Ocurrió un error inesperado. Por favor, inténtalo de nuevo más tarde.");
        }
    }

    public void forgotPassword(RecoveryRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("Si el correo existe, recibirás instrucciones"));
        recoveryService.createAndSendToken(user);
        logAccess(user, EventType.RECUPERACION, null, true);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        recoveryService.resetPassword(request.getToken(), request.getNewPassword());
    }

    @Transactional
    public void logout(String token) {
        sessionTokenRepository.findByToken(token).ifPresent(session -> {
            session.setRevoked(true);
            sessionTokenRepository.save(session);

            AccessLog log = AccessLog.builder()
                    .user(session.getUser())
                    .eventType(EventType.LOGOUT)
                    .successful(true)
                    .eventDate(LocalDateTime.now())
                    .build();
            accessLogRepository.save(log);
        });
    }

    private void logAccess(User user, EventType eventType, String ip, boolean successful) {
        AccessLog log = AccessLog.builder()
                .user(user)
                .eventType(eventType)
                .ipAddress(ip)
                .successful(successful)
                .eventDate(LocalDateTime.now())
                .build();
        accessLogRepository.save(log);
    }
}