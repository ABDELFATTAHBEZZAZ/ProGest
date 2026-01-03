package com.gestion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "devis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Devis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnoreProperties({ "devis", "factures", "hibernateLazyInitializer", "handler" })
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @JsonIgnore
    private User createdBy;

    private LocalDate dateCreation;

    private LocalDate dateValidite;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalHT = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalTVA = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalTTC = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DevisStatus statut = DevisStatus.BROUILLON;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "devis", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<DevisDetail> lignes = new ArrayList<>();

    @OneToOne(mappedBy = "devis", fetch = FetchType.LAZY)
    @JsonIgnore
    private Facture facture;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (dateCreation == null) {
            dateCreation = LocalDate.now();
        }
        if (dateValidite == null) {
            dateValidite = LocalDate.now().plusDays(30);
        }
    }

    public void calculerTotaux() {
        this.totalHT = lignes.stream()
                .map(DevisDetail::getTotalLigne)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalTVA = lignes.stream()
                .map(l -> l.getTotalLigne().multiply(l.getTva()).divide(new BigDecimal("100")))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalTTC = this.totalHT.add(this.totalTVA);
    }

    public void addLigne(DevisDetail ligne) {
        lignes.add(ligne);
        ligne.setDevis(this);
        calculerTotaux();
    }

    public void removeLigne(DevisDetail ligne) {
        lignes.remove(ligne);
        ligne.setDevis(null);
        calculerTotaux();
    }
}
