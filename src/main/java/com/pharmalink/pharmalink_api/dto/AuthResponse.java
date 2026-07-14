package com.pharmalink.pharmalink_api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String token;
    private String refreshToken;
    private String type;
    private PatientResponse patient;
}