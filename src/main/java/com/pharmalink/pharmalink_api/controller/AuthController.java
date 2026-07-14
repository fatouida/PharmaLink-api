package com.pharmalink.pharmalink_api.controller;

import com.pharmalink.pharmalink_api.dto.AuthResponse;
import com.pharmalink.pharmalink_api.dto.LoginRequest;
import com.pharmalink.pharmalink_api.dto.PatientRequest;
import com.pharmalink.pharmalink_api.dto.PatientResponse;
import com.pharmalink.pharmalink_api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/inscrire")
    public ResponseEntity<PatientResponse> inscrire(@Valid @RequestBody PatientRequest request) {
        PatientResponse response = authService.inscrire(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/connecter")
    public ResponseEntity<AuthResponse> connecter(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.connecter(request);
        return ResponseEntity.ok(response);
    }
}