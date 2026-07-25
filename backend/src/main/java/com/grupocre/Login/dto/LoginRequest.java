package com.grupocre.Login.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Se requiere nombre de usuario o correo electrónico")
    private String login;

    @NotBlank(message = "Se requiere contraseña")
    private String password;
}

