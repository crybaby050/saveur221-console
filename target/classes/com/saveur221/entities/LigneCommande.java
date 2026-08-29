package com.saveur221.entities;

public class LigneCommande {

    private int id;
    private int commandeId;
    private int produitId;
    private int quantite;

    // Dupliqué depuis Produit.prix au moment de la commande : si le prix du
    // produit change ensuite, les commandes déjà passées gardent leur montant.
    private double prixUnitaire;

    public LigneCommande() {
    }

    public LigneCommande(int id, int commandeId, int produitId, int quantite, double prixUnitaire) {
        this.id = id;
        this.commandeId = commandeId;
        this.produitId = produitId;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(int commandeId) {
        this.commandeId = commandeId;
    }

    public int getProduitId() {
        return produitId;
    }

    public void setProduitId(int produitId) {
        this.produitId = produitId;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public double calculerSousTotal() {
        return quantite * prixUnitaire;
    }

    public String toChaine() {
        StringBuilder sb = new StringBuilder();
        sb.append("LigneCommande").append("\n");
        sb.append("id : ").append(id).append("\n");
        sb.append("commandeId : ").append(commandeId).append("\n");
        sb.append("produitId : ").append(produitId).append("\n");
        sb.append("quantite : ").append(quantite).append("\n");
        sb.append("prixUnitaire : ").append(prixUnitaire).append("\n");
        sb.append("sousTotal : ").append(calculerSousTotal()).append("\n");
        return sb.toString();
    }
}