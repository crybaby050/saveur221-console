package com.saveur221.views;

import com.saveur221.entities.Categorie;
import com.saveur221.exceptions.CategorieUtiliseeException;
import com.saveur221.services.CategorieService;

import java.util.List;
import java.util.Scanner;

/**
 * Menu de gestion des catégories de produits, accessible au Gérant.
 * Regroupe les US02 à US05 du Sprint 2 : ajout, modification, suppression
 * (avec la règle métier "une catégorie contenant des produits ne peut pas
 * être supprimée"), et recherche.
 */
public class CategorieView extends MenuView {

    private final CategorieService categorieService;

    public CategorieView(CategorieService categorieService, Scanner scanner) {
        super(scanner);
        this.categorieService = categorieService;
    }

    @Override
    protected void afficherOptions() {
        System.out.println();
        System.out.println("=== Gestion des catégories ===");
        System.out.println("1. Ajouter une catégorie");
        System.out.println("2. Modifier une catégorie");
        System.out.println("3. Supprimer une catégorie");
        System.out.println("4. Rechercher une catégorie");
        System.out.println("5. Lister toutes les catégories");
        System.out.println("0. Retour au menu principal");
    }

    @Override
    protected boolean traiterChoix(int choix) {
        try {
            switch (choix) {
                case 1 -> ajouterCategorie();
                case 2 -> modifierCategorie();
                case 3 -> supprimerCategorie();
                case 4 -> rechercherCategorie();
                case 5 -> listerCategories();
                case 0 -> {
                    return false;
                }
                default -> System.out.println("Choix invalide, réessayez.");
            }
        } catch (CategorieUtiliseeException e) {
            // Règle métier affichée proprement plutôt que de faire remonter
            // une exception brute jusqu'à l'utilisateur.
            System.out.println("Erreur : " + e.getMessage());
        }
        return true;
    }

    private void ajouterCategorie() {
        System.out.println();
        System.out.println("--- Ajouter une catégorie ---");

        System.out.print("Nom : ");
        String nom = scanner.nextLine();

        System.out.print("Description : ");
        String description = scanner.nextLine();

        Categorie categorie = categorieService.ajouterCategorie(nom, description);

        System.out.println("Catégorie ajoutée avec succès :");
        System.out.println(categorie.toChaine());
    }

    // Même principe de souplesse que StockView : une saisie vide conserve
    // la valeur actuelle plutôt que d'obliger à tout ressaisir.
    private void modifierCategorie() {
        System.out.println();
        System.out.println("--- Modifier une catégorie ---");

        int id = lireEntier("Id de la catégorie à modifier : ");
        Categorie categorieActuelle = categorieService.consulterCategorie(id);

        System.out.println("Laissez un champ vide pour conserver sa valeur actuelle.");

        String nom = lireTexteOptionnel("Nom (" + categorieActuelle.getNom() + ") : ",
                categorieActuelle.getNom());

        String description = lireTexteOptionnel("Description (" + categorieActuelle.getDescription() + ") : ",
                categorieActuelle.getDescription());

        categorieService.modifierCategorie(id, nom, description);
        System.out.println("Catégorie modifiée avec succès.");
    }

    private void supprimerCategorie() {
        System.out.println();
        int id = lireEntier("Id de la catégorie à supprimer : ");

        // La règle métier (catégorie utilisée par des produits) est vérifiée
        // dans CategorieService — cette vue ne fait qu'afficher le résultat,
        // qu'il s'agisse d'un succès ou d'une CategorieUtiliseeException
        // interceptée dans traiterChoix().
        categorieService.supprimerCategorie(id);
        System.out.println("Catégorie supprimée.");
    }

    private void rechercherCategorie() {
        System.out.println();
        System.out.print("Nom à rechercher : ");
        String motCle = scanner.nextLine();

        List<Categorie> resultats = categorieService.rechercherCategorie(motCle);
        afficherListeCategories(resultats);
    }

    private void listerCategories() {
        System.out.println();
        List<Categorie> categories = categorieService.listerCategories();
        afficherListeCategories(categories);
    }

    private void afficherListeCategories(List<Categorie> categories) {
        if (categories.isEmpty()) {
            System.out.println("Aucune catégorie à afficher.");
            return;
        }

        for (Categorie categorie : categories) {
            System.out.println(categorie.toChaine());
        }
    }

}