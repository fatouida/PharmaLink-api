package com.pharmalink.pharmalink_api;

import com.pharmalink.pharmalink_api.dto.PatientRequest;
import com.pharmalink.pharmalink_api.dto.PatientResponse;
import com.pharmalink.pharmalink_api.entity.Patient;
import com.pharmalink.pharmalink_api.mapper.PatientMapper;
import com.pharmalink.pharmalink_api.repository.PatientRepository;
import com.pharmalink.pharmalink_api.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientMapper patientMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PatientService patientService;

    private PatientRequest request;
    private Patient patient;
    private PatientResponse response;

    @BeforeEach
    void setUp() {
        request = new PatientRequest();
        request.setTelephone("771234567");
        request.setNom("Diallo");
        request.setPrenom("Fatou");
        request.setMotDePasse("password123");

        patient = Patient.builder()
                .id(1L)
                .telephone("771234567")
                .nom("Diallo")
                .prenom("Fatou")
                .motDePasse("hashedPassword")
                .build();

        response = PatientResponse.builder()
                .id(1L)
                .telephone("771234567")
                .nom("Diallo")
                .prenom("Fatou")
                .build();
    }

    @Test
    void inscrire_avecTelephone_succes() {
        request.setTelephone("771234567");
        request.setEmail(null);

        when(patientRepository.existsByTelephone("771234567")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(patientMapper.toResponse(patient)).thenReturn(response);

        PatientResponse result = patientService.inscrire(request);

        assertNotNull(result);
        assertEquals("771234567", result.getTelephone());
        assertEquals("Diallo", result.getNom());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void inscrire_sanstelephoneMaisAvecEmail_succes() {
        request.setTelephone(null);
        request.setEmail("fatou@gmail.com");

        when(patientRepository.existsByEmail("fatou@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(patientMapper.toResponse(patient)).thenReturn(response);

        PatientResponse result = patientService.inscrire(request);

        assertNotNull(result);
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void inscrire_telephoneDejaUtilise_throwsException() {
        when(patientRepository.existsByTelephone("771234567")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> patientService.inscrire(request));

        assertEquals("Ce numéro est déjà utilisé", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void inscrire_emailDejaUtilise_throwsException() {
        request.setTelephone(null);
        request.setEmail("fatou@gmail.com");

        when(patientRepository.existsByEmail("fatou@gmail.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> patientService.inscrire(request));

        assertEquals("Cet email est déjà utilisé", exception.getMessage());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void inscrire_sansTelephoneNiEmail_throwsException() {
        request.setTelephone(null);
        request.setEmail(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> patientService.inscrire(request));

        assertEquals("Téléphone ou email obligatoire", exception.getMessage());
    }

    @Test
    void trouverParTelephoneOuEmail_existant_retournePatient() {
        when(patientRepository.findByTelephoneOrEmail("771234567", "771234567"))
                .thenReturn(Optional.of(patient));

        Patient result = patientService.trouverParTelephoneOuEmail("771234567");

        assertNotNull(result);
        assertEquals("771234567", result.getTelephone());
    }

    @Test
    void trouverParTelephoneOuEmail_inexistant_throwsException() {
        when(patientRepository.findByTelephoneOrEmail("000000000", "000000000"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> patientService.trouverParTelephoneOuEmail("000000000"));

        assertEquals("Patient non trouvé", exception.getMessage());
    }
}