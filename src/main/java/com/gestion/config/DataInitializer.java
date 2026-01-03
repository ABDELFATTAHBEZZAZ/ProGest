package com.gestion.config;

import com.gestion.entity.*;
import com.gestion.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;
    private final DevisRepository devisRepository;
    private final FactureRepository factureRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            initUsers();
        }
        if (clientRepository.count() == 0) {
            initClients();
        }
        if (produitRepository.count() == 0) {
            initProduits();
        }
        if (devisRepository.count() == 0) {
            initDevisAndFactures();
        }
    }

    private void initUsers() {
        // Admin
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .email("admin@gestion-maroc.com")
                .fullName("Mohamed Benjelloun")
                .role(Role.ADMIN)
                .build();
        userRepository.save(admin);

        // Vendeurs
        User vendeur1 = User.builder()
                .username("karim")
                .password(passwordEncoder.encode("karim123"))
                .email("karim.alami@gestion-maroc.com")
                .fullName("Karim Alami")
                .role(Role.UTILISATEUR)
                .build();
        userRepository.save(vendeur1);

        User vendeur2 = User.builder()
                .username("salma")
                .password(passwordEncoder.encode("salma123"))
                .email("salma.bennani@gestion-maroc.com")
                .fullName("Salma Bennani")
                .role(Role.UTILISATEUR)
                .build();
        userRepository.save(vendeur2);
    }

    private void initClients() {
        Client c1 = Client.builder()
                .nom("bezzaz")
                .email("abdelfattahbezzaz@gmail.com")
                .telephone("0676879876")
                .adresse("Rés Azzahrae 2")
                .ville("SALE")
                .codePostal("11112")
                .pays("MAROC")
                .build();

        Client c2 = Client.builder()
                .nom("anas")
                .email("anas@gmail.com")
                .telephone("0676879876")
                .adresse("Rés Azzahrae")
                .ville("SALE")
                .codePostal("11112")
                .pays("Maroc")
                .build();

        Client c3 = Client.builder()
                .nom("Maroc Telecom")
                .email("contact@iam.ma")
                .telephone("0537719000")
                .adresse("Avenue Annakhil, Rabat")
                .ville("Rabat")
                .pays("Maroc")
                .build();

        Client c4 = Client.builder()
                .nom("Groupe Addoha")
                .email("info@addoha.ma")
                .telephone("0522345678")
                .adresse("Ain Sebaa, Casablanca")
                .ville("Casablanca")
                .pays("Maroc")
                .build();

        clientRepository.saveAll(Arrays.asList(c1, c2, c3, c4));
    }

    private void initProduits() {
        Produit p1 = Produit.builder()
                .reference("SRV-DEV-001")
                .nom("Développement Site Web Vitrine")
                .description("Site web institutionnel 5 pages")
                .prixUnitaire(new BigDecimal("5000.00"))
                .tva(new BigDecimal("20.0"))
                .stock(999)
                .build();

        Produit p2 = Produit.builder()
                .reference("SRV-MAINT-002")
                .nom("Maintenance Mensuelle")
                .description("Maintenance corrective et évolutive")
                .prixUnitaire(new BigDecimal("1500.00"))
                .tva(new BigDecimal("20.0"))
                .stock(999)
                .build();

        Produit p3 = Produit.builder()
                .reference("MAT-PC-003")
                .nom("PC Portable Dell XPS")
                .description("i7, 16GB RAM, 512GB SSD")
                .prixUnitaire(new BigDecimal("18000.00"))
                .tva(new BigDecimal("20.0"))
                .stock(10)
                .build();

        Produit p4 = Produit.builder()
                .reference("LIC-OFFICE-004")
                .nom("Licence Office 365")
                .description("Abonnement annuel Business")
                .prixUnitaire(new BigDecimal("1200.00"))
                .tva(new BigDecimal("20.0"))
                .stock(50)
                .build();

        produitRepository.saveAll(Arrays.asList(p1, p2, p3, p4));
    }

    private void initDevisAndFactures() {
        User vendeur = userRepository.findByUsername("karim").orElse(null);
        Client client1 = clientRepository.findByNom("Groupe Addoha").orElse(null);
        Client client2 = clientRepository.findByNom("Maroc Telecom").orElse(null);
        Produit srvWeb = produitRepository.findByReference("SRV-DEV-001").orElse(null);
        Produit pc = produitRepository.findByReference("MAT-PC-003").orElse(null);

        if (vendeur == null || client1 == null || srvWeb == null) return;

        // Devis 1 : En attente
        Devis d1 = Devis.builder()
                .numero("DEV-202401-0001")
                .client(client1)
                .createdBy(vendeur)
                .dateCreation(LocalDate.now().minusDays(5))
                .dateValidite(LocalDate.now().plusDays(25))
                .statut(DevisStatus.ENVOYE)
                .notes("En attente de validation par M. Tazi")
                .totalHT(new BigDecimal("5000.00"))
                .totalTVA(new BigDecimal("1000.00"))
                .totalTTC(new BigDecimal("6000.00"))
                .build();
        
        DevisDetail l1 = DevisDetail.builder()
                .devis(d1)
                .produit(srvWeb)
                .quantite(1)
                .prixUnitaire(srvWeb.getPrixUnitaire())
                .tva(srvWeb.getTva())
                .remise(BigDecimal.ZERO)
                .build();
        d1.setLignes(Arrays.asList(l1));
        devisRepository.save(d1);


        // Devis 2 : Accepté -> Converti en Facture
        Devis d2 = Devis.builder()
                .numero("DEV-202401-0002")
                .client(client2)
                .createdBy(vendeur)
                .dateCreation(LocalDate.now().minusDays(10))
                .dateValidite(LocalDate.now().plusDays(20))
                .statut(DevisStatus.CONVERTI)
                .totalHT(new BigDecimal("36000.00"))
                .totalTVA(new BigDecimal("7200.00"))
                .totalTTC(new BigDecimal("43200.00"))
                .build();

        DevisDetail l2 = DevisDetail.builder()
                .devis(d2)
                .produit(pc)
                .quantite(2)
                .prixUnitaire(pc.getPrixUnitaire())
                .tva(pc.getTva())
                .remise(BigDecimal.ZERO)
                .build();
        d2.setLignes(Arrays.asList(l2));
        devisRepository.save(d2);

        // Facture associée au Devis 2
        Facture f1 = Facture.builder()
                .numero("FAC-202401-0001")
                .client(client2)
                .devis(d2)
                .createdBy(vendeur)
                .dateFacture(LocalDate.now())
                .dateEcheance(LocalDate.now().plusDays(30))
                .montantHT(d2.getTotalHT())
                .montantTVA(d2.getTotalTVA())
                .montantTTC(d2.getTotalTTC())
                .statut(FactureStatus.PAYEE)
                .modePaiement(ModePaiement.VIREMENT)
                .build();
        factureRepository.save(f1);
    }
}
