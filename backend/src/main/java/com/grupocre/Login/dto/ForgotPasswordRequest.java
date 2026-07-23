package com.grupocre.Login.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// ForgotPasswordRequest.java
@Data
public class ForgotPasswordRequest {
    @Email
    @NotBlank
    private String email;
}