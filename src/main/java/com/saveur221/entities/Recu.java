package com.saveur221.entities;

import com.saveur221.enums.TypePaiementRecu;

import java.time.LocalDateTime;

/**
 * Représente un reçu associé à un paiement.
 */
public class Recu {

    private int id;

    private String numeroRecu;

    private int paiementId;

    private TypePaiementRecu typePaiement;

    private double montant;

    private LocalDateTime dateEmission;

    public Recu() {
    }

    public Recu(
            int id,
            String numeroRecu,
            int paiementId,
            TypePaiementRecu typePaiement,
            double montant,
            LocalDateTime dateEmission
    ) {
        this.id = id;
        this.numeroRecu = numeroRecu;
        this.paiementId = paiementId;
        this.typePaiement = typePaiement;
        this.montant = montant;
        this.dateEmission = dateEmission;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroRecu() {
        return numeroRecu;
    }

    public void setNumeroRecu(String numeroRecu) {
        this.numeroRecu = numeroRecu;
    }

    public int getPaiementId() {
        return paiementId;
    }

    public void setPaiementId(int paiementId) {
        this.paiementId = paiementId;
    }

    public TypePaiementRecu getTypePaiement() {
        return typePaiement;
    }

    public void setTypePaiement(TypePaiementRecu typePaiement) {
        this.typePaiement = typePaiement;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public LocalDateTime getDateEmission() {
        return dateEmission;
    }

    public void setDateEmission(LocalDateTime dateEmission) {
        this.dateEmission = dateEmission;
    }

    /**
     * Retourne une représentation textuelle du reçu.
     *
     * @return les informations du reçu sous forme de chaîne
     */
    public String toChaine() {
        StringBuilder sb = new StringBuilder();

        sb.append("Recu").append("\n");
        sb.append("id : ").append(id).append("\n");
        sb.append("numeroRecu : ").append(numeroRecu).append("\n");
        sb.append("paiementId : ").append(paiementId).append("\n");
        sb.append("typePaiement : ").append(typePaiement).append("\n");
        sb.append("montant : ").append(montant).append("\n");
        sb.append("dateEmission : ").append(dateEmission).append("\n");

        return sb.toString();
    }
}