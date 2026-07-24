package com.grupocre.Login.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecoveryRequest {
    @Email(message = "Debe ser un correo electrónico válido")
    @NotBlank(message = "Se requiere correo electrónico")
    private String email;
}