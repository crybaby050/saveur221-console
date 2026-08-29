package com.saveur221.views;

import java.util.Scanner;

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
}