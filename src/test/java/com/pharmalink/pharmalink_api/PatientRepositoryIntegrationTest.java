package com.pharmalink.pharmalink_api;

import com.pharmalink.pharmalink_api.entity.Patient;
import com.pharmalink.pharmalink_api.enums.StatutKYC;
import com.pharmalink.pharmalink_api.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@SpringBootTest
@Testcontainers
@Transactional
class PatientRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("pharmalink_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private PatientRepository patientRepository;

    @BeforeEach
    void setUp() {
        patientRepository.deleteAll();
    }

    @Test
    void sauvegarder_patient_succes() {
        Patient patient = Patient.builder()
                .telephone("771234567")
                .nom("Diallo")
                .prenom("Fatou")
                .motDePasse("hashedPassword")
                .build();

        Patient saved = patientRepository.save(patient);

        assertNotNull(saved.getId());
        assertEquals("771234567", saved.getTelephone());
        assertEquals(StatutKYC.EN_ATTENTE, saved.getStatutKyc());
        assertFalse(saved.getActif());
    }

    @Test
    void findByTelephone_existant_retournePatient() {
        Patient patient = Patient.builder()
                .telephone("771234567")
                .nom("Diallo")
                .prenom("Fatou")
                .motDePasse("hashedPassword")
                .build();
        patientRepository.save(patient);

        Optional<Patient> result = patientRepository.findByTelephone("771234567");

        assertTrue(result.isPresent());
        assertEquals("Diallo", result.get().getNom());
    }

    @Test
    void findByTelephone_inexistant_retourneEmpty() {
        Optional<Patient> result = patientRepository.findByTelephone("000000000");
        assertFalse(result.isPresent());
    }

    @Test
    void existsByTelephone_existant_retourneTrue() {
        Patient patient = Patient.builder()
                .telephone("771234567")
                .nom("Diallo")
                .prenom("Fatou")
                .motDePasse("hashedPassword")
                .build();
        patientRepository.save(patient);

        assertTrue(patientRepository.existsByTelephone("771234567"));
    }

    @Test
    void existsByTelephone_inexistant_retourneFalse() {
        assertFalse(patientRepository.existsByTelephone("000000000"));
    }

    @Test
    void findByEmail_existant_retournePatient() {
        Patient patient = Patient.builder()
                .email("fatou@gmail.com")
                .nom("Diallo")
                .prenom("Fatou")
                .motDePasse("hashedPassword")
                .build();
        patientRepository.save(patient);

        Optional<Patient> result = patientRepository.findByEmail("fatou@gmail.com");

        assertTrue(result.isPresent());
        assertEquals("fatou@gmail.com", result.get().getEmail());
    }

    @Test
    void findByTelephoneOrEmail_avecTelephone_retournePatient() {
        Patient patient = Patient.builder()
                .telephone("771234567")
                .nom("Diallo")
                .prenom("Fatou")
                .motDePasse("hashedPassword")
                .build();
        patientRepository.save(patient);

        Optional<Patient> result = patientRepository
                .findByTelephoneOrEmail("771234567", "771234567");

        assertTrue(result.isPresent());
    }
}