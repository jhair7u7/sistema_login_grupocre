package com.grupocre.Login.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.grupocre.Login.entity.User;
import com.grupocre.Login.entity.enums.UserStatus;
import com.grupocre.Login.exception.CustomException;
import com.grupocre.Login.repository.UserRepository;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void unlockUser(Integer userId, String adminIp) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("Usuario no encontrado"));
        if (user.getEstado() != UserStatus.BLOQUEADO) {
            throw new CustomException("El usuario no está bloqueado");
        }
        user.setEstado(UserStatus.ACTIVO);
        user.setFailedAttempts(0);
        user.setLockDate(null);
        userRepository.save(user);
    }

    public List<User> getBlockedUsers() {
        return userRepository.findByEstado(UserStatus.BLOQUEADO);
    }
}