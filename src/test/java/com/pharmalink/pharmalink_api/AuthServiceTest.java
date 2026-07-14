package com.pharmalink.pharmalink_api;

import com.pharmalink.pharmalink_api.dto.AuthResponse;
import com.pharmalink.pharmalink_api.dto.LoginRequest;
import com.pharmalink.pharmalink_api.dto.PatientResponse;
import com.pharmalink.pharmalink_api.entity.Patient;
import com.pharmalink.pharmalink_api.enums.StatutKYC;
import com.pharmalink.pharmalink_api.mapper.PatientMapper;
import com.pharmalink.pharmalink_api.service.AuthService;
import com.pharmalink.pharmalink_api.service.JwtService;
import com.pharmalink.pharmalink_api.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private PatientService patientService;

    @Mock
    private PatientMapper patientMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private Patient patient;
    private PatientResponse patientResponse;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        patient = Patient.builder()
                .id(1L)
                .telephone("771234567")
                .nom("Diallo")
                .prenom("Fatou")
                .motDePasse("hashedPassword")
                .actif(true)
                .statutKyc(StatutKYC.VALIDE)
                .build();

        patientResponse = PatientResponse.builder()
                .id(1L)
                .telephone("771234567")
                .nom("Diallo")
                .prenom("Fatou")
                .build();

        loginRequest = new LoginRequest();
        loginRequest.setTelephone("771234567");
        loginRequest.setMotDePasse("password123");
    }

    @Test
    void connecter_avecTelephone_succes() {
        when(patientService.trouverParTelephoneOuEmail("771234567")).thenReturn(patient);
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtService.genererToken(patient)).thenReturn("jwt-token");
        when(jwtService.genererRefreshToken(patient)).thenReturn("refresh-token");
        when(patientMapper.toResponse(patient)).thenReturn(patientResponse);

        AuthResponse result = authService.connecter(loginRequest);

        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        assertEquals("Bearer", result.getType());
        assertNotNull(result.getPatient());
    }

    @Test
    void connecter_motDePasseIncorrect_throwsException() {
        when(patientService.trouverParTelephoneOuEmail("771234567")).thenReturn(patient);
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.connecter(loginRequest));

        assertEquals("Mot de passe incorrect", exception.getMessage());
        verify(jwtService, never()).genererToken(any());
    }

    @Test
    void connecter_compteInactif_throwsException() {
        patient.setActif(false);

        when(patientService.trouverParTelephoneOuEmail("771234567")).thenReturn(patient);
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.connecter(loginRequest));

        assertEquals("Compte en attente de validation KYC", exception.getMessage());
        verify(jwtService, never()).genererToken(any());
    }

    @Test
    void connecter_avecEmail_succes() {
        loginRequest.setTelephone(null);
        loginRequest.setEmail("fatou@gmail.com");

        when(patientService.trouverParTelephoneOuEmail("fatou@gmail.com")).thenReturn(patient);
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtService.genererToken(patient)).thenReturn("jwt-token");
        when(jwtService.genererRefreshToken(patient)).thenReturn("refresh-token");
        when(patientMapper.toResponse(patient)).thenReturn(patientResponse);

        AuthResponse result = authService.connecter(loginRequest);

        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
    }
}