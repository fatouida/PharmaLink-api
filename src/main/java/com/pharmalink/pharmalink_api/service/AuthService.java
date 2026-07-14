package com.pharmalink.pharmalink_api.service;

import com.pharmalink.pharmalink_api.dto.AuthResponse;
import com.pharmalink.pharmalink_api.dto.LoginRequest;
import com.pharmalink.pharmalink_api.dto.PatientRequest;
import com.pharmalink.pharmalink_api.dto.PatientResponse;
import com.pharmalink.pharmalink_api.entity.Patient;
import com.pharmalink.pharmalink_api.mapper.PatientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PatientService patientService;
    private final PatientMapper patientMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public PatientResponse inscrire(PatientRequest request) {
        return patientService.inscrire(request);
    }

    public AuthResponse connecter(LoginRequest request) {

        // Trouver le patient par téléphone ou email
        String credential = request.getTelephone() != null
                ? request.getTelephone()
                : request.getEmail();

        Patient patient = patientService.trouverParTelephoneOuEmail(credential);

        // Vérifier le mot de passe
        if (!passwordEncoder.matches(request.getMotDePasse(), patient.getMotDePasse())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        // Vérifier que le compte est actif
        if (!patient.getActif()) {
            throw new RuntimeException("Compte en attente de validation KYC");
        }

        // Générer le token JWT
        String token = jwtService.genererToken(patient);
        String refreshToken = jwtService.genererRefreshToken(patient);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .type("Bearer")
                .patient(patientMapper.toResponse(patient))
                .build();
    }
}