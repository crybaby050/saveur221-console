package com.saveur221.entities;

public class Categorie {

    private int id;
    private String nom;
    private String description;

    // Toujours null côté Java Console : l'upload et le stockage de
    // l'illustration (Cloudinary) sont entièrement gérés par le module PHP Web.
    private String image;

    // Toujours null côté Java Console : la colorimétrie (ex: "#8B1424") est
    // une préférence purement visuelle, définie et affichée uniquement dans
    // l'interface PHP Web.
    private String couleur;

    public Categorie() {
    }

    public Categorie(int id, String nom, String description) {
        this.id = id;
        this.nom = nom;
        this.description = description;
    }

    public Categorie(int id, String nom, String description, String image, String couleur) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.image = image;
        this.couleur = couleur;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    // Une catégorie contenant des produits ne peut pas être supprimée — cette
    // règle est vérifiée dans CategorieService, pas ici.

    public String toChaine() {
        StringBuilder sb = new StringBuilder();
        sb.append("Categorie").append("\n");
        sb.append("id : ").append(id).append("\n");
        sb.append("nom : ").append(nom).append("\n");
        sb.append("description : ").append(description).append("\n");
        return sb.toString();
        // image et couleur ne sont volontairement pas affichées ici : sans
        // intérêt pour un affichage console, elles n'ont de sens que côté PHP.
    }
}