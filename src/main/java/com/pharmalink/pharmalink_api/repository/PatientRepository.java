package com.pharmalink.pharmalink_api.repository;

import com.pharmalink.pharmalink_api.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByTelephone(String telephone);

    Optional<Patient> findByEmail(String email);

    Optional<Patient> findByTelephoneOrEmail(String telephone, String email);

    boolean existsByTelephone(String telephone);

    boolean existsByEmail(String email);
}