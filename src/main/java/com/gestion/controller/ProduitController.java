package com.gestion.controller;

import com.gestion.entity.Produit;
import com.gestion.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;

    @GetMapping
    public ResponseEntity<List<Produit>> getAllProduits() {
        return ResponseEntity.ok(produitService.getAllProduits());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Produit>> getActiveProduits() {
        return ResponseEntity.ok(produitService.getActiveProduits());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produit> getProduitById(@PathVariable Long id) {
        return ResponseEntity.ok(produitService.getProduitById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Produit>> searchProduits(@RequestParam String nom) {
        return ResponseEntity.ok(produitService.searchProduits(nom));
    }

    @GetMapping("/categorie/{categorie}")
    public ResponseEntity<List<Produit>> getProduitsByCategorie(@PathVariable String categorie) {
        return ResponseEntity.ok(produitService.getProduitsByCategorie(categorie));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<Produit>> getLowStockProduits(@RequestParam(defaultValue = "10") Integer threshold) {
        return ResponseEntity.ok(produitService.getLowStockProduits(threshold));
    }

    @PostMapping
    public ResponseEntity<Produit> createProduit(@RequestBody Produit produit) {
        return ResponseEntity.ok(produitService.createProduit(produit));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produit> updateProduit(@PathVariable Long id, @RequestBody Produit produit) {
        return ResponseEntity.ok(produitService.updateProduit(id, produit));
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Produit> updateStock(@PathVariable Long id, @RequestParam Integer quantite) {
        return ResponseEntity.ok(produitService.updateStock(id, quantite));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduit(@PathVariable Long id) {
        produitService.deleteProduit(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<Produit> toggleProduitStatus(@PathVariable Long id) {
        return ResponseEntity.ok(produitService.toggleProduitStatus(id));
    }
}
