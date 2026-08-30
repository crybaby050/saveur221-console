package com.saveur221.views;

import java.util.Scanner;

/**
 * Premier écran affiché au lancement de l'application, avant toute
 * authentification. Propose seulement deux choix : se connecter, ou
 * quitter directement — évite de forcer une saisie d'identifiants si
 * l'utilisateur veut simplement fermer le programme.
 */
public class AccueilView extends MenuView {

    // true si l'utilisateur a choisi de se connecter, false s'il a choisi
    // de quitter — lu par Main juste après l'appel à demarrer().
    private boolean choixConnexion = false;

    public AccueilView(Scanner scanner) {
        super(scanner);
    }

    @Override
    protected void afficherOptions() {
        System.out.println();
        System.out.println("=== Bienvenue sur Saveur221 ===");
        System.out.println("1. Se connecter");
        System.out.println("0. Quitter");
    }

    @Override
    protected boolean traiterChoix(int choix) {
        switch (choix) {
            case 1 -> {
                choixConnexion = true;
                return false; // arrête la boucle : on passe à la connexion
            }
            case 0 -> {
                choixConnexion = false;
                return false; // arrête la boucle : on quitte
            }
            default -> {
                System.out.println("Choix invalide, réessayez.");
                return true; // redemande
            }
        }
    }

    public boolean aChoisiDeSeConnecter() {
        return choixConnexion;
    }
}