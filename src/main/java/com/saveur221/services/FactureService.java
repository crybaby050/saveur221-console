package com.saveur221.services;

import com.saveur221.entities.Commande;
import com.saveur221.entities.Facture;
import com.saveur221.repositories.FactureRepository;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;

public class FactureService {

    private final FactureRepository factureRepository;

    public FactureService(FactureRepository factureRepository) {
        this.factureRepository = factureRepository;
    }

    // Appelée automatiquement par CommandeService juste après la création
    // d'une commande — jamais invoquée directement depuis une vue.
    public Facture genererFacture(Commande commande) {
        String numero = genererNumeroFacture();
        Facture facture = new Facture(0, numero, commande.getId(), commande.getMontantTotal(), LocalDateTime.now());
        return factureRepository.save(facture);
    }

    public Optional<Facture> consulterParCommande(int commandeId) {
        return factureRepository.findByCommandeId(commandeId);
    }

    public List<Facture> listerFactures() {
        return factureRepository.findAll();
    }

    public Optional<Facture> rechercherParNumero(String numeroFacture) {
        return factureRepository.findByNumeroFacture(numeroFacture);
    }

    // Numéro lisible du type FAC-2026-000104. Le compteur se base sur le
    // nombre de factures déjà émises — approche volontairement simple,
    // suffisante pour une application mono-utilisateur en console ; à
    // remplacer par une séquence dédiée si l'application devait un jour
    // supporter plusieurs gérants en écriture simultanée.
    private String genererNumeroFacture() {
        int nombreFactures = factureRepository.findAll().size();
        int annee = Year.now().getValue();
        return "FAC-" + annee + "-" + String.format("%06d", nombreFactures + 1);
    }
}