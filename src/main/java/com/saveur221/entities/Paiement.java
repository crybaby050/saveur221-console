package com.saveur221.entities;

import java.time.LocalDateTime;

/**
 * Représente un paiement associé à une commande.
 */
public class Paiement {

    private int id;

    private int commandeId;

    private double montant;

    private LocalDateTime datePaiement;

    public Paiement() {
    }

    public Paiement(
            int id,
            int commandeId,
            double montant,
            LocalDateTime datePaiement
    ) {
        this.id = id;
        this.commandeId = commandeId;
        this.montant = montant;
        this.datePaiement = datePaiement;
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

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
    }

    /**
     * Retourne une représentation textuelle du paiement.
     *
     * @return les informations du paiement sous forme de chaîne
     */
    public String toChaine() {
        StringBuilder sb = new StringBuilder();

        sb.append("Paiement").append("\n");
        sb.append("id : ").append(id).append("\n");
        sb.append("commandeId : ").append(commandeId).append("\n");
        sb.append("montant : ").append(montant).append("\n");
        sb.append("datePaiement : ").append(datePaiement).append("\n");

        return sb.toString();
    }
}