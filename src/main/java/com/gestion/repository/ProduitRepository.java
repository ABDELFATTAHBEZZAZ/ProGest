package com.gestion.repository;

import com.gestion.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    Optional<Produit> findByReference(String reference);

    List<Produit> findByNomContainingIgnoreCase(String nom);

    List<Produit> findByCategorie(String categorie);

    List<Produit> findByActifTrue();

    List<Produit> findByStockLessThan(Integer threshold);
}
