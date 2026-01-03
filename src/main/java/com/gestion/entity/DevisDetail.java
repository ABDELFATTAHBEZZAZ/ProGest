package com.gestion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "devis_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevisDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devis_id", nullable = false)
    @JsonIgnore
    private Devis devis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Produit produit;

    @Column(nullable = false)
    private Integer quantite;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal tva = new BigDecimal("20.00");

    private BigDecimal remise;

    public BigDecimal getTotalLigne() {
        BigDecimal total = prixUnitaire.multiply(new BigDecimal(quantite));
        if (remise != null && remise.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal remiseAmount = total.multiply(remise).divide(new BigDecimal("100"));
            total = total.subtract(remiseAmount);
        }
        return total;
    }
}
