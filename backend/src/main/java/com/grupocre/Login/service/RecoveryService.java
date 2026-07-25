package com.grupocre.Login.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.grupocre.Login.entity.PasswordResetToken;
import com.grupocre.Login.entity.User;
import com.grupocre.Login.exception.CustomException;
import com.grupocre.Login.repository.PasswordResetTokenRepository;
import com.grupocre.Login.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RecoveryService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.security.password-recovery.token-expiration-minutes}")
    private int tokenExpirationMinutes;

    @Transactional
    public void createAndSendToken(User user) {
        // Invalidate all previous unused tokens for this user
        tokenRepository.findAll().stream()
                .filter(t -> t.getUser().getId().equals(user.getId()) && !t.isUsed())
                .forEach(t -> {
                    t.setUsed(true);
                    tokenRepository.save(t);
                });

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .token(token)
                .expirationDate(LocalDateTime.now().plusMinutes(tokenExpirationMinutes))
                .used(false)
                .build();
        tokenRepository.save(resetToken);

        sendEmail(user.getEmail(), token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomException("Token inválido o caducado"));
        if (resetToken.isUsed() || resetToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new CustomException("Token inválido o caducado");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setFailedAttempts(0);
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }

    private void sendEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Recuperación de contraseña - BDCRE");
        message.setText("Para restablecer tu contraseña, usa el siguiente token:\n\n"
                + token + "\n\n"
                + "Este token expira en " + tokenExpirationMinutes + " minutos. Si no solicitaste este cambio, ignora este correo.");
        mailSender.send(message);
    }
}
