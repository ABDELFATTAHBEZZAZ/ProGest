package com.gestion.repository;

import com.gestion.entity.Devis;
import com.gestion.entity.DevisStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DevisRepository extends JpaRepository<Devis, Long> {
    List<Devis> findByClientId(Long clientId);

    List<Devis> findByStatut(DevisStatus statut);

    List<Devis> findByCreatedById(Long userId);

    @Query("SELECT d FROM Devis d WHERE d.dateCreation BETWEEN :startDate AND :endDate")
    List<Devis> findByDateRange(LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(d) FROM Devis d WHERE d.statut = :statut")
    Long countByStatut(DevisStatus statut);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(d.numero, 5) AS int)), 0) FROM Devis d WHERE d.numero LIKE :prefix%")
    Integer getMaxNumero(String prefix);
}
