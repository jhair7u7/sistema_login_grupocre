package com.grupocre.Login.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String login;     // username o email
    @NotBlank
    private String password;
}





