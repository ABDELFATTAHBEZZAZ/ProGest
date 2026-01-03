package com.gestion.service;

import com.gestion.dto.DevisRequest;
import com.gestion.dto.DevisResponse;
import com.gestion.entity.*;
import com.gestion.repository.DevisRepository;
import com.gestion.repository.DevisDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DevisService {

    private final DevisRepository devisRepository;
    private final DevisDetailRepository devisDetailRepository;
    private final ClientService clientService;
    private final ProduitService produitService;
    private final UserService userService;

    public List<Devis> getAllDevis() {
        return devisRepository.findAll();
    }

    public Devis getDevisById(Long id) {
        return devisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Devis non trouvé: " + id));
    }

    public List<Devis> getDevisByClient(Long clientId) {
        return devisRepository.findByClientId(clientId);
    }

    public List<Devis> getDevisByStatut(DevisStatus statut) {
        return devisRepository.findByStatut(statut);
    }

    private String generateNumero() {
        String prefix = "DEV-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + "-";
        Integer maxNum = devisRepository.getMaxNumero(prefix);
        int nextNum = (maxNum != null ? maxNum : 0) + 1;
        return prefix + String.format("%04d", nextNum);
    }

    @Transactional
    public Devis createDevis(DevisRequest request, String username) {
        Client client = clientService.getClientById(request.getClientId());
        User user = userService.getUserByUsername(username);

        Devis devis = Devis.builder()
                .numero(generateNumero())
                .client(client)
                .createdBy(user)
                .dateCreation(LocalDate.now())
                .dateValidite(
                        LocalDate.now().plusDays(request.getValiditeDays() != null ? request.getValiditeDays() : 30))
                .notes(request.getNotes())
                .statut(DevisStatus.BROUILLON)
                .build();

        // Add lines
        if (request.getLignes() != null) {
            for (DevisRequest.DevisLigneRequest ligneReq : request.getLignes()) {
                Produit produit = produitService.getProduitById(ligneReq.getProduitId());

                DevisDetail ligne = DevisDetail.builder()
                        .produit(produit)
                        .quantite(ligneReq.getQuantite())
                        .prixUnitaire(produit.getPrixUnitaire())
                        .tva(produit.getTva())
                        .remise(ligneReq.getRemise())
                        .build();

                devis.addLigne(ligne);
            }
        }

        return devisRepository.save(devis);
    }

    @Transactional
    public Devis updateDevis(Long id, DevisRequest request) {
        Devis devis = getDevisById(id);

        if (devis.getStatut() == DevisStatus.CONVERTI) {
            throw new RuntimeException("Impossible de modifier un devis converti en facture");
        }

        if (request.getClientId() != null) {
            Client client = clientService.getClientById(request.getClientId());
            devis.setClient(client);
        }

        devis.setNotes(request.getNotes());

        if (request.getValiditeDays() != null) {
            devis.setDateValidite(devis.getDateCreation().plusDays(request.getValiditeDays()));
        }

        // Clear and rebuild lines
        if (request.getLignes() != null) {
            devis.getLignes().clear();

            for (DevisRequest.DevisLigneRequest ligneReq : request.getLignes()) {
                Produit produit = produitService.getProduitById(ligneReq.getProduitId());

                DevisDetail ligne = DevisDetail.builder()
                        .produit(produit)
                        .quantite(ligneReq.getQuantite())
                        .prixUnitaire(produit.getPrixUnitaire())
                        .tva(produit.getTva())
                        .remise(ligneReq.getRemise())
                        .build();

                devis.addLigne(ligne);
            }
        }

        return devisRepository.save(devis);
    }

    @Transactional
    public Devis validerDevis(Long id) {
        Devis devis = getDevisById(id);
        devis.setStatut(DevisStatus.ACCEPTE);
        return devisRepository.save(devis);
    }

    @Transactional
    public Devis refuserDevis(Long id) {
        Devis devis = getDevisById(id);
        devis.setStatut(DevisStatus.REFUSE);
        return devisRepository.save(devis);
    }

    @Transactional
    public Devis envoyerDevis(Long id) {
        Devis devis = getDevisById(id);
        devis.setStatut(DevisStatus.ENVOYE);
        return devisRepository.save(devis);
    }

    @Transactional
    public void deleteDevis(Long id) {
        Devis devis = getDevisById(id);
        if (devis.getStatut() == DevisStatus.CONVERTI) {
            throw new RuntimeException("Impossible de supprimer un devis converti");
        }
        devisRepository.delete(devis);
    }

    public DevisResponse toResponse(Devis devis) {
        return DevisResponse.builder()
                .id(devis.getId())
                .numero(devis.getNumero())
                .client(DevisResponse.ClientDTO.builder()
                        .id(devis.getClient().getId())
                        .nom(devis.getClient().getNom())
                        .email(devis.getClient().getEmail())
                        .build())
                .dateCreation(devis.getDateCreation())
                .dateValidite(devis.getDateValidite())
                .totalHT(devis.getTotalHT())
                .totalTVA(devis.getTotalTVA())
                .totalTTC(devis.getTotalTTC())
                .statut(devis.getStatut().name())
                .notes(devis.getNotes())
                .lignes(devis.getLignes().stream()
                        .map(l -> DevisResponse.DevisLigneDTO.builder()
                                .id(l.getId())
                                .produitId(l.getProduit().getId())
                                .produitNom(l.getProduit().getNom())
                                .quantite(l.getQuantite())
                                .prixUnitaire(l.getPrixUnitaire())
                                .tva(l.getTva())
                                .remise(l.getRemise())
                                .totalLigne(l.getTotalLigne())
                                .build())
                        .collect(Collectors.toList()))
                .createdBy(devis.getCreatedBy() != null ? devis.getCreatedBy().getUsername() : null)
                .build();
    }
}
