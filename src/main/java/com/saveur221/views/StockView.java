package com.saveur221.views;

import com.saveur221.entities.Categorie;
import com.saveur221.entities.Produit;
import com.saveur221.exceptions.ProduitInexistantException;
import com.saveur221.services.CategorieService;
import com.saveur221.services.ProduitService;

import java.util.List;
import java.util.Scanner;

/**
 * Menu de gestion des produits et du stock, accessible au Gérant (et donc
 * à l'Administrateur, qui possède tous les droits du Gérant).
 *
 * Regroupe les US06 à US13 du Sprint 2 : CRUD produit, recherche, filtrage
 * par catégorie, consultation du stock, approvisionnement et seuil d'alerte.
 */
public class StockView extends MenuView {

    private final ProduitService produitService;
    private final CategorieService categorieService;

    public StockView(ProduitService produitService, CategorieService categorieService, Scanner scanner) {
        super(scanner);
        this.produitService = produitService;
        this.categorieService = categorieService;
    }

    @Override
    protected void afficherOptions() {
        System.out.println();
        System.out.println("=== Gestion des produits et du stock ===");
        System.out.println("1. Ajouter un produit");
        System.out.println("2. Modifier un produit");
        System.out.println("3. Supprimer un produit");
        System.out.println("4. Rechercher un produit");
        System.out.println("5. Filtrer les produits par catégorie");
        System.out.println("6. Consulter le stock");
        System.out.println("7. Approvisionner un produit");
        System.out.println("8. Définir un seuil d'alerte");
        System.out.println("0. Retour au menu principal");
    }

    @Override
    protected boolean traiterChoix(int choix) {
        try {
            switch (choix) {
                case 1 -> ajouterProduit();
                case 2 -> modifierProduit();
                case 3 -> supprimerProduit();
                case 4 -> rechercherProduit();
                case 5 -> filtrerParCategorie();
                case 6 -> consulterStock();
                case 7 -> approvisionner();
                case 8 -> definirSeuilAlerte();
                case 0 -> {
                    return false; // retour au Router
                }
                default -> System.out.println("Choix invalide, réessayez.");
            }
        } catch (ProduitInexistantException e) {
            // Toute erreur métier prévisible (id invalide, etc.) est affichée
            // proprement à l'utilisateur, sans jamais faire planter l'application.
            System.out.println("Erreur : " + e.getMessage());
        }
        return true;
    }

    private void ajouterProduit() {
        System.out.println();
        System.out.println("--- Ajouter un produit ---");

        System.out.print("Libellé : ");
        String libelle = scanner.nextLine();

        System.out.print("Description : ");
        String description = scanner.nextLine();

        double prix = lireDouble("Prix : ");
        int quantiteStock = lireEntier("Quantité en stock initiale : ");
        int seuilAlerte = lireEntier("Seuil d'alerte : ");

        int categorieId = choisirCategorie();
        if (categorieId == -1) {
            System.out.println("Ajout annulé : aucune catégorie sélectionnée.");
            return;
        }

        Produit produit = produitService.ajouterProduit(libelle, description, prix,
                quantiteStock, seuilAlerte, categorieId);

        System.out.println("Produit ajouté avec succès :");
        System.out.println(produit.toChaine());
    }

    private void modifierProduit() {
        System.out.println();
        System.out.println("--- Modifier un produit ---");

        int id = lireEntier("Id du produit à modifier : ");

        System.out.print("Nouveau libellé : ");
        String libelle = scanner.nextLine();

        System.out.print("Nouvelle description : ");
        String description = scanner.nextLine();

        double prix = lireDouble("Nouveau prix : ");

        int categorieId = choisirCategorie();
        if (categorieId == -1) {
            System.out.println("Modification annulée : aucune catégorie sélectionnée.");
            return;
        }

        produitService.modifierProduit(id, libelle, description, prix, categorieId);
        System.out.println("Produit modifié avec succès.");
    }

    private void supprimerProduit() {
        System.out.println();
        int id = lireEntier("Id du produit à supprimer : ");
        produitService.supprimerProduit(id);
        System.out.println("Produit supprimé.");
    }

    private void rechercherProduit() {
        System.out.println();
        System.out.print("Mot-clé à rechercher : ");
        String motCle = scanner.nextLine();

        List<Produit> resultats = produitService.rechercherProduit(motCle);
        afficherListeProduits(resultats);
    }

    private void filtrerParCategorie() {
        int categorieId = choisirCategorie();
        if (categorieId == -1) {
            System.out.println("Filtrage annulé : aucune catégorie sélectionnée.");
            return;
        }

        List<Produit> resultats = produitService.filtrerParCategorie(categorieId);
        afficherListeProduits(resultats);
    }

    private void consulterStock() {
        System.out.println();
        System.out.println("--- État du stock ---");

        List<Produit> tousLesProduits = produitService.listerProduits();
        afficherListeProduits(tousLesProduits);

        List<Produit> stockFaible = produitService.consulterStockFaible();
        System.out.println();
        System.out.println("Produits en stock faible (" + stockFaible.size() + ") :");
        afficherListeProduits(stockFaible);

        List<Produit> ruptures = produitService.consulterRuptures();
        System.out.println();
        System.out.println("Produits en rupture (" + ruptures.size() + ") :");
        afficherListeProduits(ruptures);
    }

    private void approvisionner() {
        System.out.println();
        int id = lireEntier("Id du produit à approvisionner : ");
        int quantite = lireEntier("Quantité à ajouter : ");

        produitService.approvisionner(id, quantite);
        System.out.println("Stock mis à jour.");
    }

    private void definirSeuilAlerte() {
        System.out.println();
        int id = lireEntier("Id du produit : ");
        int seuil = lireEntier("Nouveau seuil d'alerte : ");

        produitService.definirSeuilAlerte(id, seuil);
        System.out.println("Seuil d'alerte mis à jour.");
    }

    // Affiche la liste des catégories disponibles et retourne l'id choisi,
    // ou -1 si la saisie est invalide ou si aucune catégorie n'existe.
    private int choisirCategorie() {
        List<Categorie> categories = categorieService.listerCategories();

        if (categories.isEmpty()) {
            System.out.println("Aucune catégorie n'existe encore — créez-en une avant d'ajouter un produit.");
            return -1;
        }

        System.out.println("Catégories disponibles :");
        for (Categorie categorie : categories) {
            System.out.println(categorie.getId() + " - " + categorie.getNom());
        }

        return lireEntier("Id de la catégorie : ");
    }

    // Affiche chaque produit de la liste sur ses propres lignes (toChaine),
    // séparés par une ligne vide pour rester lisible en console.
    private void afficherListeProduits(List<Produit> produits) {
        if (produits.isEmpty()) {
            System.out.println("Aucun produit à afficher.");
            return;
        }

        for (Produit produit : produits) {
            System.out.println(produit.toChaine());
        }
    }

    // Lit un entier depuis la console, en redemandant tant que la saisie
    // n'est pas un nombre valide — évite qu'une faute de frappe ne fasse
    // planter l'application avec une NumberFormatException.
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

    private double lireDouble(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre valide.");
            }
        }
    }
}