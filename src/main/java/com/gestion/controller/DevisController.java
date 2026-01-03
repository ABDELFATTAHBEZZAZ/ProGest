package com.gestion.controller;

import com.gestion.dto.DevisRequest;
import com.gestion.dto.DevisResponse;
import com.gestion.entity.Devis;
import com.gestion.entity.DevisStatus;
import com.gestion.service.DevisService;
import com.gestion.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/devis")
@RequiredArgsConstructor
public class DevisController {

    private final DevisService devisService;
    private final PdfService pdfService;

    @GetMapping
    public ResponseEntity<List<DevisResponse>> getAllDevis() {
        List<DevisResponse> devis = devisService.getAllDevis().stream()
                .map(devisService::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(devis);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DevisResponse> getDevisById(@PathVariable Long id) {
        return ResponseEntity.ok(devisService.toResponse(devisService.getDevisById(id)));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<DevisResponse>> getDevisByClient(@PathVariable Long clientId) {
        List<DevisResponse> devis = devisService.getDevisByClient(clientId).stream()
                .map(devisService::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(devis);
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<DevisResponse>> getDevisByStatut(@PathVariable String statut) {
        DevisStatus status = DevisStatus.valueOf(statut.toUpperCase());
        List<DevisResponse> devis = devisService.getDevisByStatut(status).stream()
                .map(devisService::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(devis);
    }

    @PostMapping
    public ResponseEntity<DevisResponse> createDevis(
            @RequestBody DevisRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Devis devis = devisService.createDevis(request, userDetails.getUsername());
        return ResponseEntity.ok(devisService.toResponse(devis));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DevisResponse> updateDevis(
            @PathVariable Long id,
            @RequestBody DevisRequest request) {
        Devis devis = devisService.updateDevis(id, request);
        return ResponseEntity.ok(devisService.toResponse(devis));
    }

    @PutMapping("/{id}/valider")
    public ResponseEntity<DevisResponse> validerDevis(@PathVariable Long id) {
        return ResponseEntity.ok(devisService.toResponse(devisService.validerDevis(id)));
    }

    @PutMapping("/{id}/refuser")
    public ResponseEntity<DevisResponse> refuserDevis(@PathVariable Long id) {
        return ResponseEntity.ok(devisService.toResponse(devisService.refuserDevis(id)));
    }

    @PutMapping("/{id}/envoyer")
    public ResponseEntity<DevisResponse> envoyerDevis(@PathVariable Long id) {
        return ResponseEntity.ok(devisService.toResponse(devisService.envoyerDevis(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevis(@PathVariable Long id) {
        devisService.deleteDevis(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        Devis devis = devisService.getDevisById(id);
        byte[] pdfContent = pdfService.generateDevisPdf(devis);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "devis-" + devis.getNumero() + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdfContent);
    }
}
