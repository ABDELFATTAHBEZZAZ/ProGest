package com.gestion.repository;

import com.gestion.entity.Facture;
import com.gestion.entity.FactureStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {
    List<Facture> findByClientId(Long clientId);

    List<Facture> findByStatut(FactureStatus statut);

    List<Facture> findByCreatedById(Long userId);

    @Query("SELECT f FROM Facture f WHERE f.dateFacture BETWEEN :startDate AND :endDate")
    List<Facture> findByDateRange(LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(f) FROM Facture f WHERE f.statut = :statut")
    Long countByStatut(FactureStatus statut);

    @Query("SELECT COALESCE(SUM(f.montantTTC), 0) FROM Facture f WHERE f.statut = 'PAYEE'")
    BigDecimal getTotalChiffreAffaires();

    @Query("SELECT COALESCE(SUM(f.montantTTC), 0) FROM Facture f WHERE f.dateFacture BETWEEN :startDate AND :endDate")
    BigDecimal getChiffreAffairesByPeriod(LocalDate startDate, LocalDate endDate);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(f.numero, 5) AS int)), 0) FROM Facture f WHERE f.numero LIKE :prefix%")
    Integer getMaxNumero(String prefix);
}
