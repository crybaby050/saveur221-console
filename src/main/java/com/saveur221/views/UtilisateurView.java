package com.saveur221.views;

import com.saveur221.entities.Utilisateur;
import com.saveur221.enums.Role;
import com.saveur221.exceptions.EmailDejaUtiliseException;
import com.saveur221.exceptions.MotDePasseInvalideException;
import com.saveur221.services.UtilisateurService;

import java.util.List;
import java.util.Scanner;

/**
 * Menu de gestion des utilisateurs internes, réservé à l'Administrateur
 * (voir Router : cette vue n'est accessible que via l'option 7, elle-même
 * masquée pour un Gérant).
 * Regroupe les US22 à US28 du Sprint 3.
 */
public class UtilisateurView extends MenuView {

    private final UtilisateurService utilisateurService;

    public UtilisateurView(UtilisateurService utilisateurService, Scanner scanner) {
        super(scanner);
        this.utilisateurService = utilisateurService;
    }

    @Override
    protected void afficherOptions() {
        System.out.println();
        System.out.println("=== Gestion des utilisateurs internes ===");
        System.out.println("1. Ajouter un utilisateur");
        System.out.println("2. Modifier un utilisateur");
        System.out.println("3. Supprimer un utilisateur");
        System.out.println("4. Rechercher un utilisateur");
        System.out.println("5. Activer un utilisateur");
        System.out.println("6. Désactiver un utilisateur");
        System.out.println("7. Changer le rôle d'un utilisateur");
        System.out.println("8. Lister tous les utilisateurs");
        System.out.println("0. Retour au menu principal");
    }

    @Override
    protected boolean traiterChoix(int choix) {
        try {
            switch (choix) {
                case 1 -> ajouterUtilisateur();
                case 2 -> modifierUtilisateur();
                case 3 -> supprimerUtilisateur();
                case 4 -> rechercherUtilisateur();
                case 5 -> activerUtilisateur();
                case 6 -> desactiverUtilisateur();
                case 7 -> changerRole();
                case 8 -> listerUtilisateurs();
                case 0 -> {
                    return false;
                }
                default -> System.out.println("Choix invalide, réessayez.");
            }
        } catch (EmailDejaUtiliseException | MotDePasseInvalideException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
        return true;
    }

    private void ajouterUtilisateur() {
        System.out.println();
        System.out.println("--- Ajouter un utilisateur ---");

        System.out.print("Nom : ");
        String nom = scanner.nextLine();

        System.out.print("Prénom : ");
        String prenom = scanner.nextLine();

        System.out.print("Email : ");
        String email = scanner.nextLine();

        System.out.print("Mot de passe (min. 6 caractères) : ");
        String motDePasse = scanner.nextLine();

        Role role = choisirRole();

        Utilisateur utilisateur = utilisateurService.ajouterUtilisateur(nom, prenom, email, motDePasse, role);

        System.out.println("Utilisateur créé avec succès :");
        System.out.println(utilisateur.toChaine());
    }

    // Même principe de souplesse que les vues précédentes : une saisie
    // vide conserve la valeur actuelle. Le mot de passe n'est volontairement
    // pas modifiable ici — voir la remarque dans UtilisateurRepository.update.
    private void modifierUtilisateur() {
        System.out.println();
        System.out.println("--- Modifier un utilisateur ---");

        int id = lireEntier("Id de l'utilisateur à modifier : ");
        Utilisateur utilisateurActuel = utilisateurService.consulterUtilisateur(id);

        System.out.println("Laissez un champ vide pour conserver sa valeur actuelle.");

        String nom = lireTexteOptionnel("Nom (" + utilisateurActuel.getNom() + ") : ",
                utilisateurActuel.getNom());

        String prenom = lireTexteOptionnel("Prénom (" + utilisateurActuel.getPrenom() + ") : ",
                utilisateurActuel.getPrenom());

        String email = lireTexteOptionnel("Email (" + utilisateurActuel.getEmail() + ") : ",
                utilisateurActuel.getEmail());

        utilisateurService.modifierUtilisateur(id, nom, prenom, email);
        System.out.println("Utilisateur modifié avec succès.");
    }

    private void supprimerUtilisateur() {
        System.out.println();
        int id = lireEntier("Id de l'utilisateur à supprimer : ");
        utilisateurService.supprimerUtilisateur(id);
        System.out.println("Utilisateur supprimé.");
    }

    private void rechercherUtilisateur() {
        System.out.println();
        System.out.print("Nom ou prénom à rechercher : ");
        String motCle = scanner.nextLine();

        List<Utilisateur> resultats = utilisateurService.rechercherUtilisateur(motCle);
        afficherListeUtilisateurs(resultats);
    }

    private void activerUtilisateur() {
        System.out.println();
        int id = lireEntier("Id de l'utilisateur à activer : ");
        utilisateurService.activer(id);
        System.out.println("Utilisateur activé.");
    }

    private void desactiverUtilisateur() {
        System.out.println();
        int id = lireEntier("Id de l'utilisateur à désactiver : ");
        utilisateurService.desactiver(id);
        System.out.println("Utilisateur désactivé — il ne pourra plus se connecter.");
    }

    private void changerRole() {
        System.out.println();
        int id = lireEntier("Id de l'utilisateur : ");
        Role nouveauRole = choisirRole();

        utilisateurService.changerRole(id, nouveauRole);
        System.out.println("Rôle mis à jour.");
    }

    private void listerUtilisateurs() {
        System.out.println();
        List<Utilisateur> utilisateurs = utilisateurService.listerUtilisateurs();
        afficherListeUtilisateurs(utilisateurs);
    }

    // Affiche les valeurs de l'enum Role sous forme de menu numéroté, plutôt
    // que de demander à l'utilisateur de taper "ADMIN" ou "GERANT" à la main.
    private Role choisirRole() {
        Role[] roles = Role.values();

        System.out.println("Rôles disponibles :");
        for (int i = 0; i < roles.length; i++) {
            System.out.println((i + 1) + ". " + roles[i]);
        }

        while (true) {
            int choix = lireEntier("Votre choix : ");
            if (choix >= 1 && choix <= roles.length) {
                return roles[choix - 1];
            }
            System.out.println("Choix invalide, réessayez.");
        }
    }

    private void afficherListeUtilisateurs(List<Utilisateur> utilisateurs) {
        if (utilisateurs.isEmpty()) {
            System.out.println("Aucun utilisateur à afficher.");
            return;
        }

        for (Utilisateur utilisateur : utilisateurs) {
            System.out.println(utilisateur.toChaine());
        }
    }

}