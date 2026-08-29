package com.saveur221.views;

import com.saveur221.config.Container;
import com.saveur221.entities.Utilisateur;
import com.saveur221.enums.Role;

import java.util.Scanner;

/**
 * Menu principal affiché après connexion, dont le contenu dépend du rôle
 * de l'utilisateur connecté (ADMIN voit une option supplémentaire que
 * GERANT n'a pas).
 *
 * Ne contient aucune logique métier : son seul travail est d'orienter
 * l'utilisateur vers le bon sous-menu (StockView, CommandeView, etc.).
 * Chaque sous-menu est lui-même une MenuView, appelée via demarrer() —
 * une fois cet appel terminé (l'utilisateur choisit "Retour" dans le
 * sous-menu), le contrôle revient ici et le menu principal se réaffiche.
 */
public class Router extends MenuView {

    private final Container container;
    private final Utilisateur utilisateurConnecte;

    public Router(Container container, Utilisateur utilisateurConnecte, Scanner scanner) {
        super(scanner);
        this.container = container;
        this.utilisateurConnecte = utilisateurConnecte;
    }

    @Override
    protected void afficherOptions() {
        System.out.println();
        System.out.println("=== Menu principal (" + utilisateurConnecte.getRole() + ") ===");
        System.out.println("1. Gestion des catégories");
        System.out.println("2. Gestion des produits et du stock");
        System.out.println("3. Gestion des commandes");
        System.out.println("4. Gestion des paiements");
        System.out.println("5. Consulter les factures");
        System.out.println("6. Consulter les statistiques");

        // Option réservée à l'Administrateur — cohérent avec la règle
        // métier "l'espace admin est réservé au rôle ADMIN".
        if (utilisateurConnecte.getRole() == Role.ADMIN) {
            System.out.println("7. Gestion des utilisateurs internes");
        }

        System.out.println("0. Se déconnecter");
    }

    @Override
    protected boolean traiterChoix(int choix) {
        switch (choix) {
            // Chaque sous-menu concret (StockView, CommandeView, ...) sera
            // branché ici au fur et à mesure qu'on l'écrira — pour l'instant,
            // un message temporaire évite de référencer des classes qui
            // n'existent pas encore.
            case 1 -> System.out.println("Menu Catégories — à venir.");
            case 2 -> new StockView(container.getProduitService(), container.getCategorieService(), scanner).demarrer();
            case 3 -> System.out.println("Menu Commandes — à venir.");
            case 4 -> System.out.println("Menu Paiements — à venir.");
            case 5 -> System.out.println("Menu Factures — à venir.");
            case 6 -> System.out.println("Statistiques — à venir.");
            case 7 -> {
                // Double vérification : même si l'option n'est pas affichée
                // pour un GERANT, rien n'empêche de taper "7" au clavier.
                if (utilisateurConnecte.getRole() == Role.ADMIN) {
                    System.out.println("Menu Utilisateurs — à venir.");
                } else {
                    System.out.println("Option invalide.");
                }
            }
            case 0 -> {
                System.out.println("Déconnexion...");
                return false; // arrête la boucle de MenuView.demarrer()
            }
            default -> System.out.println("Choix invalide, réessayez.");
        }
        return true;
    }
}