package com.saveur221.entities;

import com.saveur221.enums.Role;

public class Utilisateur {

    private int id;
    private String nom;
    private String prenom;

    // Règle métier : l'unicité de l'email est vérifiée en amont, dans le service,
    // pas ici — cette classe ne fait que porter la donnée.
    private String email;

    // Toujours stocké haché (jamais en clair) — le hachage est réalisé dans
    // AuthService.
    private String motDePasse;

    // Un compte désactivé ne peut pas se connecter (vérifié dans AuthService).
    private boolean actif;

    // Détermine le menu affiché après connexion (ADMIN ou GERANT).
    private Role role;

    public Utilisateur() {
    }

    public Utilisateur(int id, String nom, String prenom, String email,
            String motDePasse, boolean actif, Role role) {
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

    // Affichage multi-lignes utilisé par les vues console (menu utilisateurs, etc.)
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