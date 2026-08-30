package com.saveur221.entities;

import java.time.LocalDateTime;

/**
 * Représente une facture associée à une commande.
 */
public class Facture {

    private int id;

    private String numeroFacture;

    private int commandeId;

    private double montantTotal;

    private LocalDateTime dateEmission;

    public Facture() {
    }

    public Facture(
            int id,
            String numeroFacture,
            int commandeId,
            double montantTotal,
            LocalDateTime dateEmission
    ) {
        this.id = id;
        this.numeroFacture = numeroFacture;
        this.commandeId = commandeId;
        this.montantTotal = montantTotal;
        this.dateEmission = dateEmission;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroFacture() {
        return numeroFacture;
    }

    public void setNumeroFacture(String numeroFacture) {
        this.numeroFacture = numeroFacture;
    }

    public int getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(int commandeId) {
        this.commandeId = commandeId;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public LocalDateTime getDateEmission() {
        return dateEmission;
    }

    public void setDateEmission(LocalDateTime dateEmission) {
        this.dateEmission = dateEmission;
    }

    /**
     * Retourne une représentation textuelle de la facture.
     *
     * @return les informations de la facture sous forme de chaîne
     */
    public String toChaine() {
        StringBuilder sb = new StringBuilder();

        sb.append("Facture").append("\n");
        sb.append("id : ").append(id).append("\n");
        sb.append("numeroFacture : ").append(numeroFacture).append("\n");
        sb.append("commandeId : ").append(commandeId).append("\n");
        sb.append("montantTotal : ").append(montantTotal).append("\n");
        sb.append("dateEmission : ").append(dateEmission).append("\n");

        return sb.toString();
    }
}