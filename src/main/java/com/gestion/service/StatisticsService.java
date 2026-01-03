package com.gestion.service;

import com.gestion.dto.StatisticsDTO;
import com.gestion.entity.DevisStatus;
import com.gestion.entity.FactureStatus;
import com.gestion.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;
    private final DevisRepository devisRepository;
    private final FactureRepository factureRepository;
    private final FactureService factureService;

    public StatisticsDTO getStatistics() {
        return StatisticsDTO.builder()
                .totalClients(clientRepository.count())
                .totalProduits(produitRepository.count())
                .totalDevis(devisRepository.count())
                .totalFactures(factureRepository.count())
                .devisBrouillon(devisRepository.countByStatut(DevisStatus.BROUILLON))
                .devisAcceptes(devisRepository.countByStatut(DevisStatus.ACCEPTE))
                .facturesPayees(factureRepository.countByStatut(FactureStatus.PAYEE))
                .facturesNonPayees(factureRepository.countByStatut(FactureStatus.NON_PAYEE))
                .chiffreAffaires(factureService.getChiffreAffaires())
                .chiffreAffairesMois(factureService.getChiffreAffairesMois())
                .utilisateursConnectes((long) userRepository.findByConnectedTrue().size())
                .totalUtilisateurs(userRepository.count())
                .build();
    }
}
