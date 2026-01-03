package com.gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevisResponse {
    private Long id;
    private String numero;
    private ClientDTO client;
    private LocalDate dateCreation;
    private LocalDate dateValidite;
    private BigDecimal totalHT;
    private BigDecimal totalTVA;
    private BigDecimal totalTTC;
    private String statut;
    private String notes;
    private List<DevisLigneDTO> lignes;
    private String createdBy;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClientDTO {
        private Long id;
        private String nom;
        private String email;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DevisLigneDTO {
        private Long id;
        private Long produitId;
        private String produitNom;
        private Integer quantite;
        private BigDecimal prixUnitaire;
        private BigDecimal tva;
        private BigDecimal remise;
        private BigDecimal totalLigne;
    }
}
