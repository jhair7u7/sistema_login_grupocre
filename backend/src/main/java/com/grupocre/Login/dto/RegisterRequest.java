package com.grupocre.Login.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Se requiere el nombre")
    private String nombre;

    @NotBlank(message = "Se requiere el apellido")
    private String apellido;

    @Email(message = "Debe ser un correo electrónico válido")
    @NotBlank(message = "Se requiere correo electrónico")
    private String email;

    @NotBlank(message = "Se requiere nombre de usuario")
    @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
    private String username;

    @NotBlank(message = "Se requiere contraseña")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
}