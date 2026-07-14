package com.pharmalink.pharmalink_api;

import com.pharmalink.pharmalink_api.entity.Patient;
import com.pharmalink.pharmalink_api.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private Patient patient;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        patient = Patient.builder()
                .id(1L)
                .telephone("771234567")
                .nom("Diallo")
                .prenom("Fatou")
                .build();
    }

    @Test
    void genererToken_retourneTokenNonNull() {
        String token = jwtService.genererToken(patient);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void genererRefreshToken_retourneTokenNonNull() {
        String token = jwtService.genererRefreshToken(patient);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extraireSubject_retourneTelephone() {
        String token = jwtService.genererToken(patient);
        String subject = jwtService.extraireSubject(token);
        assertEquals("771234567", subject);
    }

    @Test
    void extraireSubject_avecEmail_retourneEmail() {
        patient.setTelephone(null);
        patient.setEmail("fatou@gmail.com");

        String token = jwtService.genererToken(patient);
        String subject = jwtService.extraireSubject(token);

        assertEquals("fatou@gmail.com", subject);
    }

    @Test
    void estValide_tokenValide_retourneTrue() {
        String token = jwtService.genererToken(patient);
        assertTrue(jwtService.estValide(token));
    }

    @Test
    void estValide_tokenInvalide_retourneFalse() {
        assertFalse(jwtService.estValide("token.invalide.ici"));
    }

    @Test
    void estValide_tokenVide_retourneFalse() {
        assertFalse(jwtService.estValide(""));
    }
}