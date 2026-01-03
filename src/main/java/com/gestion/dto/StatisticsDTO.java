package com.gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO {
    private Long totalClients;
    private Long totalProduits;
    private Long totalDevis;
    private Long totalFactures;
    private Long devisBrouillon;
    private Long devisAcceptes;
    private Long facturesPayees;
    private Long facturesNonPayees;
    private BigDecimal chiffreAffaires;
    private BigDecimal chiffreAffairesMois;
    private Long utilisateursConnectes;
    private Long totalUtilisateurs;
}
