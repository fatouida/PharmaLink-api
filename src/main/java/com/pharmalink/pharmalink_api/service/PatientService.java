package com.pharmalink.pharmalink_api.service;

import com.pharmalink.pharmalink_api.dto.PatientRequest;
import com.pharmalink.pharmalink_api.dto.PatientResponse;
import com.pharmalink.pharmalink_api.entity.Patient;
import com.pharmalink.pharmalink_api.mapper.PatientMapper;
import com.pharmalink.pharmalink_api.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final PasswordEncoder passwordEncoder;

    public PatientResponse inscrire(PatientRequest request) {

        // Vérifier qu'au moins téléphone ou email est fourni
        if ((request.getTelephone() == null || request.getTelephone().isBlank()) &&
                (request.getEmail() == null || request.getEmail().isBlank())) {
            throw new RuntimeException("Téléphone ou email obligatoire");
        }

        // Vérifier que le téléphone n'est pas déjà utilisé
        if (request.getTelephone() != null && !request.getTelephone().isBlank()) {
            if (patientRepository.existsByTelephone(request.getTelephone())) {
                throw new RuntimeException("Ce numéro est déjà utilisé");
            }
        }

        // Vérifier que l'email n'est pas déjà utilisé
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (patientRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Cet email est déjà utilisé");
            }
        }

        // Créer le patient
        Patient patient = Patient.builder()
                .telephone(request.getTelephone())
                .email(request.getEmail())
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .adresse(request.getAdresse())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .build();

        Patient saved = patientRepository.save(patient);
        return patientMapper.toResponse(saved);
    }

    public Patient trouverParTelephoneOuEmail(String credential) {
        return patientRepository
                .findByTelephoneOrEmail(credential, credential)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
    }
}