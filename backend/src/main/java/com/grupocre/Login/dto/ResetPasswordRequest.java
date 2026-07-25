package com.grupocre.Login.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "Se requiere un token")
    private String token;

    @NotBlank(message = "Se requiere una nueva contraseña")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String newPassword;
}
