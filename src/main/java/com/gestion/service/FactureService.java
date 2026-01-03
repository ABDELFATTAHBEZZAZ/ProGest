package com.gestion.service;

import com.gestion.entity.*;
import com.gestion.repository.FactureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FactureService {

    private final FactureRepository factureRepository;
    private final DevisService devisService;
    private final ClientService clientService;
    private final UserService userService;
    private final ProduitService produitService;

    public List<Facture> getAllFactures() {
        return factureRepository.findAll();
    }

    public Facture getFactureById(Long id) {
        return factureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée: " + id));
    }

    public List<Facture> getFacturesByClient(Long clientId) {
        return factureRepository.findByClientId(clientId);
    }

    public List<Facture> getFacturesByStatut(FactureStatus statut) {
        return factureRepository.findByStatut(statut);
    }

    private String generateNumero() {
        String prefix = "FAC-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + "-";
        Integer maxNum = factureRepository.getMaxNumero(prefix);
        int nextNum = (maxNum != null ? maxNum : 0) + 1;
        return prefix + String.format("%04d", nextNum);
    }

    @Transactional
    public Facture createFromDevis(Long devisId, String username) {
        Devis devis = devisService.getDevisById(devisId);

        if (devis.getStatut() == DevisStatus.CONVERTI) {
            throw new RuntimeException("Ce devis a déjà été converti en facture");
        }

        if (devis.getStatut() != DevisStatus.ACCEPTE) {
            throw new RuntimeException("Le devis doit être accepté avant conversion");
        }

        User user = userService.getUserByUsername(username);

        Facture facture = Facture.builder()
                .numero(generateNumero())
                .client(devis.getClient())
                .devis(devis)
                .createdBy(user)
                .dateFacture(LocalDate.now())
                .dateEcheance(LocalDate.now().plusDays(30))
                .montantHT(devis.getTotalHT())
                .montantTVA(devis.getTotalTVA())
                .montantTTC(devis.getTotalTTC())
                .statut(FactureStatus.NON_PAYEE)
                .build();

        facture = factureRepository.save(facture);

        // Update devis status
        devis.setStatut(DevisStatus.CONVERTI);

        // Decrement stock
        if (devis.getLignes() != null) {
            for (DevisDetail ligne : devis.getLignes()) {
                produitService.decrementStock(ligne.getProduit().getId(), ligne.getQuantite());
            }
        }

        return facture;
    }

    @Transactional
    public Facture createDirecte(Long clientId, BigDecimal montantHT, BigDecimal tva, String notes, String username) {
        Client client = clientService.getClientById(clientId);
        User user = userService.getUserByUsername(username);

        BigDecimal montantTVA = montantHT.multiply(tva).divide(new BigDecimal("100"));
        BigDecimal montantTTC = montantHT.add(montantTVA);

        Facture facture = Facture.builder()
                .numero(generateNumero())
                .client(client)
                .createdBy(user)
                .dateFacture(LocalDate.now())
                .dateEcheance(LocalDate.now().plusDays(30))
                .montantHT(montantHT)
                .montantTVA(montantTVA)
                .montantTTC(montantTTC)
                .statut(FactureStatus.NON_PAYEE)
                .notes(notes)
                .build();

        return factureRepository.save(facture);
    }

    @Transactional
    public Facture enregistrerPaiement(Long id, BigDecimal montant, ModePaiement modePaiement) {
        Facture facture = getFactureById(id);

        if (facture.getStatut() == FactureStatus.PAYEE) {
            throw new RuntimeException("Cette facture est déjà entièrement payée");
        }

        facture.setModePaiement(modePaiement);
        facture.addPaiement(montant);

        return factureRepository.save(facture);
    }

    @Transactional
    public Facture annulerFacture(Long id) {
        Facture facture = getFactureById(id);
        facture.setStatut(FactureStatus.ANNULEE);
        return factureRepository.save(facture);
    }

    @Transactional
    public void deleteFacture(Long id) {
        Facture facture = getFactureById(id);
        factureRepository.delete(facture);
    }

    public BigDecimal getChiffreAffaires() {
        return factureRepository.getTotalChiffreAffaires();
    }

    public BigDecimal getChiffreAffairesMois() {
        LocalDate debut = LocalDate.now().withDayOfMonth(1);
        LocalDate fin = LocalDate.now();
        return factureRepository.getChiffreAffairesByPeriod(debut, fin);
    }
}
