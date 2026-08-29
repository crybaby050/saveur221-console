package com.saveur221.entities;

/**
 * Catégorie de produits (ex. "Plats", "Boissons", "Desserts").
 *
 * <p>Une catégorie ne peut pas être supprimée tant qu'elle contient des
 * produits — règle vérifiée dans {@code CategorieService}, en complément
 * de la contrainte {@code ON DELETE RESTRICT} posée en base à titre de
 * filet de sécurité.</p>
 */
public class Categorie {

    private int id;
    private String nom;
    private String description;

    public Categorie() {
    }

    public Categorie(int id, String nom, String description) {
        this.id = id;
        this.nom = nom;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Categorie{id=" + id + ", nom='" + nom + "'}";
    }
}