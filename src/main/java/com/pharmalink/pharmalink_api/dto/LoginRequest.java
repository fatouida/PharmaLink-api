package com.pharmalink.pharmalink_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    private String telephone;

    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;
}