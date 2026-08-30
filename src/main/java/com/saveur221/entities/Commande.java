package com.saveur221.entities;

import com.saveur221.enums.StatutCommande;
import com.saveur221.enums.StatutPaiement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente une commande passée par un client.
 */
public class Commande {

    private int id;

    private String numeroCommande;

    private int clientId;

    private LocalDateTime dateCommande;

    private StatutCommande statut;

    private StatutPaiement statutPaiement;

    private double montantTotal;

    private List<LigneCommande> lignes = new ArrayList<>();

    public Commande() {
    }

    public Commande(
            int id,
            String numeroCommande,
            int clientId,
            LocalDateTime dateCommande,
            StatutCommande statut,
            StatutPaiement statutPaiement,
            double montantTotal
    ) {
        this.id = id;
        this.numeroCommande = numeroCommande;
        this.clientId = clientId;
        this.dateCommande = dateCommande;
        this.statut = statut;
        this.statutPaiement = statutPaiement;
        this.montantTotal = montantTotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroCommande() {
        return numeroCommande;
    }

    public void setNumeroCommande(String numeroCommande) {
        this.numeroCommande = numeroCommande;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDateTime dateCommande) {
        this.dateCommande = dateCommande;
    }

    public StatutCommande getStatut() {
        return statut;
    }

    public void setStatut(StatutCommande statut) {
        this.statut = statut;
    }

    public StatutPaiement getStatutPaiement() {
        return statutPaiement;
    }

    public void setStatutPaiement(StatutPaiement statutPaiement) {
        this.statutPaiement = statutPaiement;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public List<LigneCommande> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneCommande> lignes) {
        this.lignes = lignes;
    }

    public void ajouterLigne(LigneCommande ligne) {
        this.lignes.add(ligne);
    }

    /**
     * Calcule le montant total de la commande à partir de ses lignes.
     *
     * @return le montant total calculé
     */
    public double calculerMontantTotal() {
        return lignes.stream()
                .mapToDouble(ligne -> ligne.getPrixUnitaire() * ligne.getQuantite())
                .sum();
    }

    /**
     * Retourne une représentation textuelle de la commande.
     *
     * @return les informations de la commande sous forme de chaîne
     */
    public String toChaine() {
        StringBuilder sb = new StringBuilder();

        sb.append("Commande").append("\n");
        sb.append("id : ").append(id).append("\n");
        sb.append("numeroCommande : ").append(numeroCommande).append("\n");
        sb.append("clientId : ").append(clientId).append("\n");
        sb.append("dateCommande : ").append(dateCommande).append("\n");
        sb.append("statut : ").append(statut).append("\n");
        sb.append("statutPaiement : ").append(statutPaiement).append("\n");
        sb.append("montantTotal : ").append(montantTotal).append("\n");
        sb.append("nombreDeLignes : ").append(lignes.size()).append("\n");

        return sb.toString();
    }
}