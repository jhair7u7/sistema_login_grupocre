package com.grupocre.Login.security;


import com.grupocre.Login.entity.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails extends User {

    private final Long userId;
    private final String email;

    public CustomUserDetails(Usuario usuario) {
        super(usuario.getUsername(),
                usuario.getPassword(),
                usuario.getActivo(),
                true, true,
                !usuario.getBloqueado(),  // accountNonLocked = !bloqueado
                mapRoles(usuario));
        this.userId = usuario.getId();
        this.email = usuario.getEmail();
    }

    private static Collection<? extends GrantedAuthority> mapRoles(Usuario usuario) {
        return usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
                .collect(Collectors.toList());
    }

    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
}