package com.saveur221.views;

import com.saveur221.entities.Utilisateur;
import com.saveur221.exceptions.CompteDesactiveException;
import com.saveur221.exceptions.MotDePasseIncorrectException;
import com.saveur221.exceptions.UtilisateurInexistantException;
import com.saveur221.services.AuthService;

import java.util.Scanner;

public class LoginView {

    private final AuthService authService;
    private final Scanner scanner;

    public LoginView(AuthService authService, Scanner scanner) {
        this.authService = authService;
        this.scanner = scanner;
    }

    public Utilisateur demarrerConnexion() {
        System.out.println("=== Connexion — Saveur221 ===");

        while (true) {
            System.out.print("Email : ");
            String email = scanner.nextLine();

            System.out.print("Mot de passe : ");
            String motDePasse = scanner.nextLine();

            try {
                Utilisateur utilisateur = authService.authentifier(email, motDePasse);
                System.out.println("Connexion réussie. Bienvenue " + utilisateur.getPrenom() +
                        " (" + utilisateur.getRole() + ")");
                return utilisateur;

            } catch (UtilisateurInexistantException | MotDePasseIncorrectException e) {
                System.out.println("Erreur : " + e.getMessage());
            } catch (CompteDesactiveException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }
}