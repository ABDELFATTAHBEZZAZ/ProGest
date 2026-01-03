package com.gestion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevisRequest {
    private Long clientId;
    private String notes;
    private Integer validiteDays;
    private List<DevisLigneRequest> lignes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DevisLigneRequest {
        private Long produitId;
        private Integer quantite;
        private BigDecimal remise;
    }
}
