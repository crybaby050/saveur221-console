package com.saveur221.entities;

public class Produit {

    private int id;
    private String libelle;
    private String description;
    private double prix;
    private int quantiteStock;

    // En dessous de ce seuil, le produit est signalé "stock faible" dans les vues.
    private int seuilAlerte;

    // Recalculé automatiquement à chaque mouvement de stock (voir méthodes
    // ci-dessous) — ne jamais le modifier manuellement depuis un service.
    private boolean disponible;

    // Toujours null côté Java Console : l'upload et le stockage des images
    // (Cloudinary) sont entièrement gérés par le module PHP Web.
    private String image;

    private int categorieId;

    public Produit() {
    }

    public Produit(int id, String libelle, String description, double prix, int quantiteStock,
            int seuilAlerte, boolean disponible, String image, int categorieId) {
        this.id = id;
        this.libelle = libelle;
        this.description = description;
        this.prix = prix;
        this.quantiteStock = quantiteStock;
        this.seuilAlerte = seuilAlerte;
        this.disponible = disponible;
        this.image = image;
        this.categorieId = categorieId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getQuantiteStock() {
        return quantiteStock;
    }

    public void setQuantiteStock(int quantiteStock) {
        this.quantiteStock = quantiteStock;
    }

    public int getSeuilAlerte() {
        return seuilAlerte;
    }

    public void setSeuilAlerte(int seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(int categorieId) {
        this.categorieId = categorieId;
    }

    // Augmente le stock (US "Approvisionner un produit") et met à jour la
    // disponibilité en conséquence.
    public void approvisionner(int quantite) {
        this.quantiteStock += quantite;
        recalculerDisponibilite();
    }

    // Diminue le stock lors d'une vente (création de commande).
    public void diminuerStock(int quantite) {
        this.quantiteStock -= quantite;
        recalculerDisponibilite();
    }

    // Restitue le stock lors de l'annulation d'une commande.
    public void restaurerStock(int quantite) {
        this.quantiteStock += quantite;
        recalculerDisponibilite();
    }

    // Règle métier : si quantiteStock = 0, le produit devient indisponible.
    private void recalculerDisponibilite() {
        this.disponible = this.quantiteStock > 0;
    }

    public boolean estEnRupture() {
        return quantiteStock == 0;
    }

    public boolean estStockFaible() {
        return quantiteStock > 0 && quantiteStock <= seuilAlerte;
    }

    public String toChaine() {
        StringBuilder sb = new StringBuilder();
        sb.append("Produit").append("\n");
        sb.append("id : ").append(id).append("\n");
        sb.append("libelle : ").append(libelle).append("\n");
        sb.append("description : ").append(description).append("\n");
        sb.append("prix : ").append(prix).append("\n");
        sb.append("quantiteStock : ").append(quantiteStock).append("\n");
        sb.append("seuilAlerte : ").append(seuilAlerte).append("\n");
        sb.append("disponible : ").append(disponible).append("\n");
        sb.append("categorieId : ").append(categorieId).append("\n");
        return sb.toString();
    }
}