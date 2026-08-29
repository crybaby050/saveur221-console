package com.saveur221.views;

import com.saveur221.entities.Categorie;
import com.saveur221.entities.Produit;
import com.saveur221.exceptions.ProduitInexistantException;
import com.saveur221.services.CategorieService;
import com.saveur221.services.ProduitService;

import java.util.List;
import java.util.Scanner;

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
                    return false;
                }
                default -> System.out.println("Choix invalide, réessayez.");
            }
        } catch (ProduitInexistantException e) {
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

    // Chaque champ affiche l'ancienne valeur entre parenthèses ; une saisie
    // vide (Entrée directe) conserve cette valeur telle quelle, plutôt que
    // d'obliger à tout ressaisir pour ne changer qu'un seul champ.
    private void modifierProduit() {
        System.out.println();
        System.out.println("--- Modifier un produit ---");

        int id = lireEntier("Id du produit à modifier : ");
        Produit produitActuel = produitService.consulterProduit(id);

        System.out.println("Laissez un champ vide pour conserver sa valeur actuelle.");

        String libelle = lireTexteOptionnel("Libellé (" + produitActuel.getLibelle() + ") : ",
                produitActuel.getLibelle());

        String description = lireTexteOptionnel("Description (" + produitActuel.getDescription() + ") : ",
                produitActuel.getDescription());

        double prix = lireDoubleOptionnel("Prix (" + produitActuel.getPrix() + ") : ",
                produitActuel.getPrix());

        int categorieId = choisirCategorieOptionnelle(produitActuel.getCategorieId());

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

    // Même liste que choisirCategorie(), mais avec conservation de la
    // catégorie actuelle si l'utilisateur laisse la saisie vide.
    private int choisirCategorieOptionnelle(int categorieActuelleId) {
        List<Categorie> categories = categorieService.listerCategories();

        System.out.println("Catégories disponibles :");
        for (Categorie categorie : categories) {
            String marqueur = categorie.getId() == categorieActuelleId ? " (actuelle)" : "";
            System.out.println(categorie.getId() + " - " + categorie.getNom() + marqueur);
        }

        System.out.print("Id de la catégorie (laisser vide pour conserver l'actuelle) : ");
        String saisie = scanner.nextLine().trim();

        if (saisie.isEmpty()) {
            return categorieActuelleId;
        }

        try {
            return Integer.parseInt(saisie);
        } catch (NumberFormatException e) {
            System.out.println("Saisie invalide, catégorie actuelle conservée.");
            return categorieActuelleId;
        }
    }

    private void afficherListeProduits(List<Produit> produits) {
        if (produits.isEmpty()) {
            System.out.println("Aucun produit à afficher.");
            return;
        }

        for (Produit produit : produits) {
            System.out.println(produit.toChaine());
        }
    }

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

    // Version "optionnelle" de la saisie texte : une entrée vide conserve
    // la valeur actuelle passée en paramètre.
    private String lireTexteOptionnel(String message, String valeurActuelle) {
        System.out.print(message);
        String saisie = scanner.nextLine();
        return saisie.isBlank() ? valeurActuelle : saisie;
    }

    // Version "optionnelle" de la saisie numérique : une entrée vide ou
    // invalide conserve la valeur actuelle plutôt que de redemander en boucle
    // (comportement volontairement différent de lireDouble(), qui lui
    // redemande jusqu'à obtenir une valeur — ici, vide = choix assumé de
    // ne pas modifier, pas une erreur de saisie).
    private double lireDoubleOptionnel(String message, double valeurActuelle) {
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