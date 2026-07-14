package com.pharmalink.pharmalink_api.entity;

import com.pharmalink.pharmalink_api.enums.StatutDocument;
import com.pharmalink.pharmalink_api.enums.TypeDocument;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents_kyc")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentKYC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeDocument typeDocument;

    @Column(nullable = false)
    private String urlRecto;

    private String urlVerso;

    private String urlSelfie;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutDocument statut = StatutDocument.EN_ATTENTE;

    private String adminValidateur;
    private String motifRejet;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime dateUpload = LocalDateTime.now();

    private LocalDateTime dateValidation;
}