package com.saveur221.entities;

/**
 * Représente un client de l'application.
 */
public class Client {

    private int id;

    private String nom;

    private String prenom;

    private String email;

    private String motDePasse;

    private String telephone;

    private String adresse;

    public Client() {
    }

    public Client(
            int id,
            String nom,
            String prenom,
            String email,
            String motDePasse,
            String telephone,
            String adresse
    ) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.telephone = telephone;
        this.adresse = adresse;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    /**
     * Retourne une représentation textuelle du client.
     *
     * @return les informations du client sous forme de chaîne
     */
    public String toChaine() {
        StringBuilder sb = new StringBuilder();

        sb.append("Client").append("\n");
        sb.append("id : ").append(id).append("\n");
        sb.append("nom : ").append(nom).append("\n");
        sb.append("prenom : ").append(prenom).append("\n");
        sb.append("email : ").append(email).append("\n");
        sb.append("telephone : ").append(telephone).append("\n");
        sb.append("adresse : ").append(adresse).append("\n");

        return sb.toString();
    }
}