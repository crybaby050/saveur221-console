package com.saveur221.services;

import com.saveur221.entities.Commande;
import com.saveur221.entities.Paiement;
import com.saveur221.enums.StatutPaiement;
import com.saveur221.enums.TypePaiementRecu;
import com.saveur221.exceptions.CommandeInexistanteException;
import com.saveur221.exceptions.MontantPaiementInvalideException;
import com.saveur221.repositories.CommandeRepository;
import com.saveur221.repositories.PaiementRepository;

import java.time.LocalDateTime;
import java.util.List;

public class PaiementService {

    // FactureService n'est pas injecté ici : ce service ne dépend que de ce
    // dont il a réellement besoin (principe de responsabilité minimale).
    private final PaiementRepository paiementRepository;
    private final CommandeRepository commandeRepository;
    private final RecuService recuService;

    public PaiementService(PaiementRepository paiementRepository,
                            CommandeRepository commandeRepository,
                            RecuService recuService) {
        this.paiementRepository = paiementRepository;
        this.commandeRepository = commandeRepository;
        this.recuService = recuService;
    }

    public List<Paiement> consulterParCommande(int commandeId) {
        return paiementRepository.findByCommandeId(commandeId);
    }

    public List<Commande> consulterCommandesImpayees() {
        return commandeRepository.findImpayeesOuPartielles();
    }

    public Paiement enregistrerPaiement(int commandeId, double montant) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new CommandeInexistanteException("Commande introuvable avec l'id " + commandeId));

        double totalDejaPaye = paiementRepository.sommePaiements(commandeId);
        double montantRestant = commande.getMontantTotal() - totalDejaPaye;

        // Règle métier centrale : un paiement ne peut jamais dépasser le
        // solde restant à payer.
        if (montant > montantRestant) {
            throw new MontantPaiementInvalideException(
                    "Le montant saisi (" + montant + ") dépasse le solde restant (" + montantRestant + ").");
        }

        Paiement paiement = new Paiement(0, commandeId, montant, LocalDateTime.now());
        paiement = paiementRepository.save(paiement);

        double nouveauTotalPaye = totalDejaPaye + montant;
        boolean soldeComplet = nouveauTotalPaye >= commande.getMontantTotal();

        // Met à jour le statut de paiement de la commande — indépendant de
        // son statut de préparation (StatutCommande).
        commande.setStatutPaiement(soldeComplet ? StatutPaiement.PAYEE : StatutPaiement.PARTIEL);
        commandeRepository.update(commande);

        // Un reçu est émis à chaque paiement, qu'il solde totalement la
        // commande ou non — jamais d'exception à cette règle.
        TypePaiementRecu typePaiement = soldeComplet ? TypePaiementRecu.TOTAL : TypePaiementRecu.PARTIEL;
        recuService.genererRecu(paiement, typePaiement);

        return paiement;
    }
}