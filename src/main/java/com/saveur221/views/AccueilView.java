package com.saveur221.views;

import com.saveur221.enums.ChoixAccueil;

import java.util.Scanner;

/**
 * Premier écran affiché au lancement de l'application, avant toute
 * authentification. Propose seulement deux choix : se connecter, ou
 * quitter directement.
 *
 * Ne hérite volontairement pas de MenuView : le patron Template Method de
 * MenuView est conçu pour des menus qui exécutent une action puis reviennent
 * (contrat void), alors que cet écran doit renvoyer un résultat exploitable
 * par l'appelant (Main). Forcer cette classe dans le moule de MenuView
 * aurait nécessité un état caché pour faire remonter le choix — moins
 * clair qu'une méthode qui retourne directement sa réponse.
 */
public class AccueilView {

    private final Scanner scanner;

    public AccueilView(Scanner scanner) {
        this.scanner = scanner;
    }

    // Boucle jusqu'à obtenir un choix valide, puis retourne directement
    // le résultat — pas de champ intermédiaire à consulter après coup.
    public ChoixAccueil demander() {
        while (true) {
            System.out.println();
            System.out.println("=== Bienvenue sur Saveur221 ===");
            System.out.println("1. Se connecter");
            System.out.println("0. Quitter");
            System.out.print("Votre choix : ");

            String saisie = scanner.nextLine().trim();

            switch (saisie) {
                case "1" -> {
                    return ChoixAccueil.SE_CONNECTER;
                }
                case "0" -> {
                    return ChoixAccueil.QUITTER;
                }
                default -> System.out.println("Choix invalide, réessayez.");
            }
        }
    }
}