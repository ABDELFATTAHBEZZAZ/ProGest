package com.gestion.service;

import com.gestion.entity.Produit;
import com.gestion.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProduitService {

    private final ProduitRepository produitRepository;

    public List<Produit> getAllProduits() {
        return produitRepository.findAll();
    }

    public List<Produit> getActiveProduits() {
        return produitRepository.findByActifTrue();
    }

    public Produit getProduitById(Long id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé: " + id));
    }

    public List<Produit> searchProduits(String nom) {
        return produitRepository.findByNomContainingIgnoreCase(nom);
    }

    public List<Produit> getProduitsByCategorie(String categorie) {
        return produitRepository.findByCategorie(categorie);
    }

    public List<Produit> getLowStockProduits(Integer threshold) {
        return produitRepository.findByStockLessThan(threshold);
    }

    @Transactional
    public Produit createProduit(Produit produit) {
        return produitRepository.save(produit);
    }

    @Transactional
    public Produit updateProduit(Long id, Produit produitDetails) {
        Produit produit = getProduitById(id);

        produit.setNom(produitDetails.getNom());
        produit.setDescription(produitDetails.getDescription());
        produit.setPrixUnitaire(produitDetails.getPrixUnitaire());
        produit.setStock(produitDetails.getStock());
        produit.setReference(produitDetails.getReference());
        produit.setCategorie(produitDetails.getCategorie());
        produit.setTva(produitDetails.getTva());
        produit.setActif(produitDetails.isActif());

        return produitRepository.save(produit);
    }

    @Transactional
    public Produit updateStock(Long id, Integer quantite) {
        Produit produit = getProduitById(id);
        produit.setStock(produit.getStock() + quantite);
        return produitRepository.save(produit);
    }

    @Transactional
    public void decrementStock(Long id, Integer quantite) {
        Produit produit = getProduitById(id);
        if (produit.getStock() < quantite) {
            throw new RuntimeException("Stock insuffisant pour le produit: " + produit.getNom());
        }
        produit.setStock(produit.getStock() - quantite);
        produitRepository.save(produit);
    }

    @Transactional
    public void deleteProduit(Long id) {
        Produit produit = getProduitById(id);
        produitRepository.delete(produit);
    }

    @Transactional
    public Produit toggleProduitStatus(Long id) {
        Produit produit = getProduitById(id);
        produit.setActif(!produit.isActif());
        return produitRepository.save(produit);
    }
}
