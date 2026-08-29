package com.saveur221.views;

import java.util.Scanner;

/**
 * Base commune à tous les écrans de menu de l'application (Stock, Commandes,
 * Utilisateurs, Factures, etc.).
 *
 * Applique le patron de conception "Template Method" : cette classe abstraite
 * fixe le déroulement général (afficher le menu, lire un choix, le traiter,
 * recommencer) et délègue les parties spécifiques à chaque sous-classe via
 * les méthodes abstraites afficherOptions() et traiterChoix(...).
 *
 * Elle centralise aussi les méthodes de saisie (lireEntier, lireDouble, et
 * leurs variantes "optionnelles" qui conservent une valeur existante si
 * l'utilisateur laisse le champ vide) — utilisées à l'identique par toutes
 * les vues concrètes, pour éviter de dupliquer ce code dans chacune d'elles.
 */
public abstract class MenuView {

    protected final Scanner scanner;

    protected MenuView(Scanner scanner) {
        this.scanner = scanner;
    }

    public final void demarrer() {
        boolean continuer = true;

        while (continuer) {
            afficherOptions();
            int choix = lireChoix();
            continuer = traiterChoix(choix);
        }
    }

    protected abstract void afficherOptions();

    protected abstract boolean traiterChoix(int choix);

    protected int lireChoix() {
        System.out.print("Votre choix : ");
        String saisie = scanner.nextLine();

        try {
            return Integer.parseInt(saisie.trim());
        } catch (NumberFormatException e) {
            System.out.println("Saisie invalide, veuillez entrer un nombre.");
            return -1;
        }
    }

    // Lit un entier depuis la console, en redemandant tant que la saisie
    // n'est pas un nombre valide — évite qu'une faute de frappe ne fasse
    // planter l'application avec une NumberFormatException.
    protected int lireEntier(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre entier valide.");
            }
        }
    }

    protected double lireDouble(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre valide.");
            }
        }
    }

    // Version "optionnelle" de la saisie texte : une entrée vide conserve
    // la valeur actuelle passée en paramètre, plutôt que d'obliger à tout
    // ressaisir pour ne modifier qu'un seul champ.
    protected String lireTexteOptionnel(String message, String valeurActuelle) {
        System.out.print(message);
        String saisie = scanner.nextLine();
        return saisie.isBlank() ? valeurActuelle : saisie;
    }

    // Version "optionnelle" de la saisie numérique : une entrée vide ou
    // invalide conserve la valeur actuelle plutôt que de redemander en
    // boucle — ici, vide = choix assumé de ne pas modifier, pas une erreur.
    protected double lireDoubleOptionnel(String message, double valeurActuelle) {
        System.out.print(message);
        String saisie = scanner.nextLine().trim();

        if (saisie.isEmpty()) {
            return valeurActuelle;
        }

        try {
            return Double.parseDouble(saisie);
        } catch (NumberFormatException e) {
            System.out.println("Saisie invalide, valeur actuelle conservée.");
            return valeurActuelle;
        }
    }
}