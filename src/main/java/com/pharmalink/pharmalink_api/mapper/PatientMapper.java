package com.pharmalink.pharmalink_api.mapper;

import com.pharmalink.pharmalink_api.dto.PatientResponse;
import com.pharmalink.pharmalink_api.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public PatientResponse toResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .telephone(patient.getTelephone())
                .email(patient.getEmail())
                .nom(patient.getNom())
                .prenom(patient.getPrenom())
                .adresse(patient.getAdresse())
                .statutKyc(patient.getStatutKyc())
                .actif(patient.getActif())
                .createdAt(patient.getCreatedAt())
                .build();
    }
}