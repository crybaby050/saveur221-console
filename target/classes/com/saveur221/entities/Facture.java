package com.saveur221.entities;

import java.time.LocalDateTime;

public class Facture {

    private int id;

    // Référence lisible générée par FactureService, ex: FAC-2026-000104.
    private String numeroFacture;

    // Relation 1-1 stricte : commandeId est contraint UNIQUE en base.
    private int commandeId;

    // Montant de la commande figé au moment de l'émission de la facture.
    private double montantTotal;

    private LocalDateTime dateEmission;

    // Portée actuelle : uniquement les commandes créées depuis le Java
    // Console (vente au comptoir). Le module PHP Web ne génère pas encore
    // de facture pour ses propres commandes.

    public Facture() {
    }

    public Facture(int id, String numeroFacture, int commandeId, double montantTotal, LocalDateTime dateEmission) {
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