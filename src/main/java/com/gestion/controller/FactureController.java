package com.gestion.controller;

import com.gestion.entity.Facture;
import com.gestion.entity.FactureStatus;
import com.gestion.entity.ModePaiement;
import com.gestion.service.FactureService;
import com.gestion.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/factures")
@RequiredArgsConstructor
public class FactureController {

    private final FactureService factureService;
    private final PdfService pdfService;

    @GetMapping
    public ResponseEntity<List<Facture>> getAllFactures() {
        return ResponseEntity.ok(factureService.getAllFactures());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Facture> getFactureById(@PathVariable Long id) {
        return ResponseEntity.ok(factureService.getFactureById(id));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Facture>> getFacturesByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(factureService.getFacturesByClient(clientId));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<Facture>> getFacturesByStatut(@PathVariable String statut) {
        FactureStatus status = FactureStatus.valueOf(statut.toUpperCase());
        return ResponseEntity.ok(factureService.getFacturesByStatut(status));
    }

    @PostMapping("/from-devis/{devisId}")
    public ResponseEntity<Facture> createFromDevis(
            @PathVariable Long devisId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(factureService.createFromDevis(devisId, userDetails.getUsername()));
    }

    @PostMapping("/directe")
    public ResponseEntity<Facture> createDirecte(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long clientId = Long.valueOf(request.get("clientId").toString());
        BigDecimal montantHT = new BigDecimal(request.get("montantHT").toString());
        BigDecimal tva = new BigDecimal(request.getOrDefault("tva", "20").toString());
        String notes = (String) request.get("notes");

        return ResponseEntity
                .ok(factureService.createDirecte(clientId, montantHT, tva, notes, userDetails.getUsername()));
    }

    @PutMapping("/{id}/paiement")
    public ResponseEntity<Facture> enregistrerPaiement(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        BigDecimal montant = new BigDecimal(request.get("montant").toString());
        ModePaiement modePaiement = ModePaiement.valueOf(request.get("modePaiement").toString().toUpperCase());

        return ResponseEntity.ok(factureService.enregistrerPaiement(id, montant, modePaiement));
    }

    @PutMapping("/{id}/annuler")
    public ResponseEntity<Facture> annulerFacture(@PathVariable Long id) {
        return ResponseEntity.ok(factureService.annulerFacture(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacture(@PathVariable Long id) {
        factureService.deleteFacture(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        Facture facture = factureService.getFactureById(id);
        byte[] pdfContent = pdfService.generateFacturePdf(facture);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "facture-" + facture.getNumero() + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdfContent);
    }
}
