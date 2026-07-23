package com.grupocre.Login.service;


import com.grupocre.Login.dto.UserProfile;
import com.grupocre.Login.entity.Usuario;
import com.grupocre.Login.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UsuarioRepository usuarioRepository;

    public UserProfile getProfile(String username) {
        Usuario user = usuarioRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return new UserProfile(user);
    }
}