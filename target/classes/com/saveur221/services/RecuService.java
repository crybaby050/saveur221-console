package com.saveur221.services;

import com.saveur221.entities.Paiement;
import com.saveur221.entities.Recu;
import com.saveur221.enums.TypePaiementRecu;
import com.saveur221.repositories.RecuRepository;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;

public class RecuService {

    private final RecuRepository recuRepository;

    public RecuService(RecuRepository recuRepository) {
        this.recuRepository = recuRepository;
    }

    // Appelée automatiquement par PaiementService juste après
    // l'enregistrement d'un paiement — le type (PARTIEL/TOTAL) est déterminé
    // en amont par PaiementService, qui seul connaît le solde de la commande.
    public Recu genererRecu(Paiement paiement, TypePaiementRecu typePaiement) {
        String numero = genererNumeroRecu();
        Recu recu = new Recu(0, numero, paiement.getId(), typePaiement, paiement.getMontant(), LocalDateTime.now());
        return recuRepository.save(recu);
    }

    public List<Recu> consulterParCommande(int commandeId) {
        return recuRepository.findByCommandeId(commandeId);
    }

    public Optional<Recu> consulterParPaiement(int paiementId) {
        return recuRepository.findByPaiementId(paiementId);
    }

    public List<Recu> listerRecus() {
        return recuRepository.findAll();
    }

    // Même logique que pour les factures : compteur simple basé sur le
    // nombre de reçus déjà émis.
    private String genererNumeroRecu() {
        int nombreRecus = recuRepository.findAll().size();
        int annee = Year.now().getValue();
        return "REC-" + annee + "-" + String.format("%06d", nombreRecus + 1);
    }
}