package com.saveur221.entities;

import com.saveur221.enums.Role;

/**
 * Représente un utilisateur de l'application.
 */
public class Utilisateur {

    private int id;

    private String nom;

    private String prenom;

    private String email;

    private String motDePasse;

    private boolean actif;

    private Role role;

    public Utilisateur() {
    }

    public Utilisateur(
            int id,
            String nom,
            String prenom,
            String email,
            String motDePasse,
            boolean actif,
            Role role
    ) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.actif = actif;
        this.role = role;
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Retourne une représentation textuelle de l'utilisateur.
     *
     * @return les informations de l'utilisateur sous forme de chaîne
     */
    public String toChaine() {
        StringBuilder sb = new StringBuilder();

        sb.append("Utilisateur").append("\n");
        sb.append("id : ").append(id).append("\n");
        sb.append("nom : ").append(nom).append("\n");
        sb.append("prenom : ").append(prenom).append("\n");
        sb.append("email : ").append(email).append("\n");
        sb.append("actif : ").append(actif).append("\n");
        sb.append("role : ").append(role).append("\n");

        return sb.toString();
    }
}