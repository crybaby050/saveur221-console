package com.saveur221.views;

import com.saveur221.entities.Facture;
import com.saveur221.services.FactureService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Menu de consultation des factures, accessible au Gérant.
 *
 * Entièrement en lecture seule : une facture est toujours générée
 * automatiquement par CommandeService au moment de la création d'une
 * commande sur place (voir CommandeView) — ce menu ne propose donc ni
 * ajout, ni modification, ni suppression.
 */
public class FactureView extends MenuView {

    private final FactureService factureService;

    public FactureView(FactureService factureService, Scanner scanner) {
        super(scanner);
        this.factureService = factureService;
    }

    @Override
    protected void afficherOptions() {
        System.out.println();
        System.out.println("=== Consultation des factures ===");
        System.out.println("1. Lister toutes les factures");
        System.out.println("2. Rechercher une facture par numéro");
        System.out.println("3. Consulter la facture d'une commande");
        System.out.println("0. Retour au menu principal");
    }

    @Override
    protected boolean traiterChoix(int choix) {
        switch (choix) {
            case 1 -> listerFactures();
            case 2 -> rechercherParNumero();
            case 3 -> consulterParCommande();
            case 0 -> {
                return false;
            }
            default -> System.out.println("Choix invalide, réessayez.");
        }
        return true;
    }

    private void listerFactures() {
        System.out.println();
        List<Facture> factures = factureService.listerFactures();

        if (factures.isEmpty()) {
            System.out.println("Aucune facture n'a encore été émise.");
            return;
        }

        for (Facture facture : factures) {
            System.out.println(facture.toChaine());
        }
    }

    private void rechercherParNumero() {
        System.out.println();
        System.out.print("Numéro de facture (ex: FAC-2026-000104) : ");
        String numero = scanner.nextLine();

        Optional<Facture> facture = factureService.rechercherParNumero(numero);

        if (facture.isEmpty()) {
            System.out.println("Aucune facture trouvée avec ce numéro.");
            return;
        }

        System.out.println(facture.get().toChaine());
    }

    private void consulterParCommande() {
        System.out.println();
        int commandeId = lireEntier("Id de la commande : ");

        Optional<Facture> facture = factureService.consulterParCommande(commandeId);

        if (facture.isEmpty()) {
            System.out.println("Aucune facture associée à cette commande.");
            return;
        }

        System.out.println(facture.get().toChaine());
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
}