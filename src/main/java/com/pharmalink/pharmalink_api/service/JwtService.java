package com.pharmalink.pharmalink_api.service;

import com.pharmalink.pharmalink_api.entity.Patient;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final long EXPIRATION = 1000 * 60 * 15;
    private static final long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24 * 7;
    private final SecretKey key = Jwts.SIG.HS256.key().build();

    public String genererToken(Patient patient) {
        return buildToken(new HashMap<>(), patient, EXPIRATION);
    }

    public String genererRefreshToken(Patient patient) {
        return buildToken(new HashMap<>(), patient, REFRESH_EXPIRATION);
    }

    private String buildToken(Map<String, Object> claims, Patient patient, long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(patient.getTelephone() != null
                        ? patient.getTelephone()
                        : patient.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public String extraireSubject(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean estValide(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}