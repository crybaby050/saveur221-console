package com.saveur221.entities;

/**
 * Représente une catégorie de produits.
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retourne une représentation textuelle de la catégorie.
     *
     * @return les informations de la catégorie sous forme de chaîne
     */
    public String toChaine() {
        StringBuilder sb = new StringBuilder();

        sb.append("Categorie").append("\n");
        sb.append("id : ").append(id).append("\n");
        sb.append("nom : ").append(nom).append("\n");
        sb.append("description : ").append(description).append("\n");

        return sb.toString();
    }
}