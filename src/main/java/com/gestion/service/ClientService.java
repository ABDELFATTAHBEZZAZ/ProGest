package com.gestion.service;

import com.gestion.entity.Client;
import com.gestion.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé: " + id));
    }

    public List<Client> searchClients(String search) {
        return clientRepository.searchClients(search);
    }

    @Transactional
    public Client createClient(Client client) {
        if (client.getEmail() != null && clientRepository.findByEmail(client.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }
        return clientRepository.save(client);
    }

    @Transactional
    public Client updateClient(Long id, Client clientDetails) {
        Client client = getClientById(id);

        client.setNom(clientDetails.getNom());
        client.setEmail(clientDetails.getEmail());
        client.setTelephone(clientDetails.getTelephone());
        client.setAdresse(clientDetails.getAdresse());
        client.setVille(clientDetails.getVille());
        client.setCodePostal(clientDetails.getCodePostal());
        client.setPays(clientDetails.getPays());

        return clientRepository.save(client);
    }

    @Transactional
    public void deleteClient(Long id) {
        Client client = getClientById(id);
        clientRepository.delete(client);
    }
}
