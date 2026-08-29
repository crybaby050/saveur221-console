package com.saveur221.views;

import com.saveur221.services.CommandeService;
import com.saveur221.services.CommandeService.Statistiques;

import java.util.Scanner;

/**
 * Écran de consultation du tableau de bord statistique (US21), accessible
 * au Gérant. Entièrement en lecture seule — aucune saisie hormis le choix
 * de retour, donc pas besoin d'un menu à plusieurs options : un seul
 * affichage complet, suivi de l'attente d'une touche pour revenir.
 */
public class StatistiqueView extends MenuView {

    private final CommandeService commandeService;

    public StatistiqueView(CommandeService commandeService, Scanner scanner) {
        super(scanner);
        this.commandeService = commandeService;
    }

    @Override
    protected void afficherOptions() {
        System.out.println();
        System.out.println("=== Tableau de bord statistique ===");

        Statistiques stats = commandeService.calculerStatistiques();

        System.out.println("Chiffre d'affaires du jour : " + stats.chiffreAffairesJour);
        System.out.println("Chiffre d'affaires de la semaine : " + stats.chiffreAffairesSemaine);
        System.out.println("Chiffre d'affaires du mois : " + stats.chiffreAffairesMois);
        System.out.println("Nombre de commandes total : " + stats.nombreCommandes);
        System.out.println("Commandes en cours : " + stats.commandesEnCours);
        System.out.println("Produit le plus vendu : " + stats.produitLePlusVendu);

        System.out.println("Top 3 des produits :");
        if (stats.top3Produits.isEmpty()) {
            System.out.println("  Aucune donnée disponible.");
        } else {
            for (String ligne : stats.top3Produits) {
                System.out.println("  - " + ligne);
            }
        }

        System.out.println();
        System.out.println("0. Retour au menu principal");
    }

    @Override
    protected boolean traiterChoix(int choix) {
        // Un seul choix valide ici : quitter. Tout le reste réaffiche
        // simplement le tableau de bord (les chiffres peuvent avoir changé
        // entre-temps si une commande a été passée ailleurs).
        return choix != 0;
    }
}