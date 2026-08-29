package com.saveur221.views;

import com.saveur221.entities.Commande;
import com.saveur221.entities.Paiement;
import com.saveur221.entities.Recu;
import com.saveur221.exceptions.CommandeInexistanteException;
import com.saveur221.exceptions.MontantPaiementInvalideException;
import com.saveur221.services.PaiementService;
import com.saveur221.services.RecuService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Menu de gestion des paiements, accessible au Gérant.
 * Regroupe US19 ("Enregistrer un paiement") et US20 ("Consulter les
 * commandes impayées"), ainsi que la consultation des reçus générés
 * automatiquement à chaque paiement (voir RecuService).
 */
public class PaiementView extends MenuView {

    private final PaiementService paiementService;
    private final RecuService recuService;

    public PaiementView(PaiementService paiementService, RecuService recuService, Scanner scanner) {
        super(scanner);
        this.paiementService = paiementService;
        this.recuService = recuService;
    }

    @Override
    protected void afficherOptions() {
        System.out.println();
        System.out.println("=== Gestion des paiements ===");
        System.out.println("1. Enregistrer un paiement");
        System.out.println("2. Consulter les commandes impayées");
        System.out.println("3. Consulter les paiements d'une commande");
        System.out.println("4. Consulter les reçus d'une commande");
        System.out.println("0. Retour au menu principal");
    }

    @Override
    protected boolean traiterChoix(int choix) {
        try {
            switch (choix) {
                case 1 -> enregistrerPaiement();
                case 2 -> consulterCommandesImpayees();
                case 3 -> consulterPaiementsCommande();
                case 4 -> consulterRecusCommande();
                case 0 -> {
                    return false;
                }
                default -> System.out.println("Choix invalide, réessayez.");
            }
        } catch (CommandeInexistanteException | MontantPaiementInvalideException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return true;
    }

    private void enregistrerPaiement() {
        System.out.println();
        System.out.println("--- Enregistrer un paiement ---");

        int commandeId = lireEntier("Id de la commande : ");
        double montant = lireDouble("Montant du paiement : ");

        Paiement paiement = paiementService.enregistrerPaiement(commandeId, montant);

        System.out.println("Paiement enregistré avec succès :");
        System.out.println(paiement.toChaine());

        // Le reçu a été généré automatiquement par PaiementService — cette
        // vue le récupère juste pour en afficher le numéro et le type
        // (PARTIEL/TOTAL) à l'utilisateur.
        Optional<Recu> recu = recuService.consulterParPaiement(paiement.getId());
        recu.ifPresent(r -> {
            System.out.println("Reçu généré : " + r.getNumeroRecu() + " (" + r.getTypePaiement() + ")");
        });
    }

    private void consulterCommandesImpayees() {
        System.out.println();
        List<Commande> commandes = paiementService.consulterCommandesImpayees();

        if (commandes.isEmpty()) {
            System.out.println("Aucune commande impayée ou partiellement payée.");
            return;
        }

        for (Commande commande : commandes) {
            System.out.println(commande.toChaine());
        }
    }

    private void consulterPaiementsCommande() {
        System.out.println();
        int commandeId = lireEntier("Id de la commande : ");

        List<Paiement> paiements = paiementService.consulterParCommande(commandeId);

        if (paiements.isEmpty()) {
            System.out.println("Aucun paiement enregistré pour cette commande.");
            return;
        }

        for (Paiement paiement : paiements) {
            System.out.println(paiement.toChaine());
        }
    }

    // Une commande peut avoir plusieurs reçus (un par paiement) — cette
    // méthode les affiche tous, dans l'ordre chronologique d'émission.
    private void consulterRecusCommande() {
        System.out.println();
        int commandeId = lireEntier("Id de la commande : ");

        List<Recu> recus = recuService.consulterParCommande(commandeId);

        if (recus.isEmpty()) {
            System.out.println("Aucun reçu pour cette commande.");
            return;
        }

        for (Recu recu : recus) {
            System.out.println(recu.toChaine());
        }
    }

    private int lireEntier(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre entier valide.");
            }
        }
    }

    private double lireDouble(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre valide.");
            }
        }
    }
}