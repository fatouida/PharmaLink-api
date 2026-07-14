package com.pharmalink.pharmalink_api.dto;

import com.pharmalink.pharmalink_api.enums.StatutKYC;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PatientResponse {

    private Long id;
    private String telephone;
    private String email;
    private String nom;
    private String prenom;
    private String adresse;
    private StatutKYC statutKyc;
    private Boolean actif;
    private LocalDateTime createdAt;
}