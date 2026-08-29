package com.saveur221.services;

import com.saveur221.entities.Client;
import com.saveur221.exceptions.ClientInexistantException;
import com.saveur221.repositories.ClientRepository;

import java.util.List;

public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> listerClients() {
        return clientRepository.findAll();
    }

    public Client consulterClient(int id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ClientInexistantException("Client introuvable avec l'id " + id));
    }

    public Client rechercherParEmail(String email) {
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> new ClientInexistantException("Aucun client trouvé avec l'email " + email));
    }

    public List<Client> rechercherParTelephone(String motCle) {
        return clientRepository.rechercherParTelephone(motCle);
    }
}