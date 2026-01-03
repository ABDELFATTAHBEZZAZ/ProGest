package com.gestion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "factures")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnoreProperties({ "devis", "factures", "hibernateLazyInitializer", "handler" })
    private Client client;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devis_id")
    @JsonIgnore
    private Devis devis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @JsonIgnore
    private User createdBy;

    private LocalDate dateFacture;

    private LocalDate dateEcheance;

    @Column(precision = 10, scale = 2)
    private BigDecimal montantHT;

    @Column(precision = 10, scale = 2)
    private BigDecimal montantTVA;

    @Column(precision = 10, scale = 2)
    private BigDecimal montantTTC;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal montantPaye = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private FactureStatus statut = FactureStatus.NON_PAYEE;

    @Enumerated(EnumType.STRING)
    private ModePaiement modePaiement;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (dateFacture == null) {
            dateFacture = LocalDate.now();
        }
        if (dateEcheance == null) {
            dateEcheance = LocalDate.now().plusDays(30);
        }
    }

    public BigDecimal getMontantRestant() {
        return montantTTC.subtract(montantPaye);
    }

    public void addPaiement(BigDecimal montant) {
        this.montantPaye = this.montantPaye.add(montant);
        if (this.montantPaye.compareTo(this.montantTTC) >= 0) {
            this.statut = FactureStatus.PAYEE;
            this.paidAt = LocalDateTime.now();
        } else if (this.montantPaye.compareTo(BigDecimal.ZERO) > 0) {
            this.statut = FactureStatus.PARTIELLEMENT_PAYEE;
        }
    }
}
