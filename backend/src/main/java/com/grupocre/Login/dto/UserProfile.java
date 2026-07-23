package com.grupocre.Login.dto;


import com.grupocre.Login.entity.Usuario;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserProfile {
    private Long id;
    private String username;
    private String email;
    private String nombre;
    private String apellido;
    private boolean activo;
    private Set<String> roles;

    public UserProfile(Usuario usuario) {
        this.id = usuario.getId();
        this.username = usuario.getUsername();
        this.email = usuario.getEmail();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.activo = usuario.getActivo();
        this.roles = usuario.getRoles().stream()
                .map(rol -> rol.getNombre())
                .collect(Collectors.toSet());
    }
}