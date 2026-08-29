package com.saveur221.services;

import com.saveur221.entities.Utilisateur;
import com.saveur221.enums.Role;
import com.saveur221.exceptions.EmailDejaUtiliseException;
import com.saveur221.exceptions.MotDePasseInvalideException;
import com.saveur221.repositories.UtilisateurRepository;

import java.util.List;
import java.util.Optional;

public class UtilisateurService {

    private static final int LONGUEUR_MIN_MOT_DE_PASSE = 6;

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<Utilisateur> listerUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public List<Utilisateur> rechercherUtilisateur(String motCle) {
        return utilisateurRepository.rechercherParNom(motCle);
    }

    public Utilisateur ajouterUtilisateur(String nom, String prenom, String email,
                                           String motDePasse, Role role) {
        // Règle métier : l'email doit être unique.
        Optional<Utilisateur> existant = utilisateurRepository.findByEmail(email);
        if (existant.isPresent()) {
            throw new EmailDejaUtiliseException("Cet email est déjà utilisé : " + email);
        }

        // Règle métier : le mot de passe doit contenir au moins 6 caractères.
        if (motDePasse.length() < LONGUEUR_MIN_MOT_DE_PASSE) {
            throw new MotDePasseInvalideException(
                    "Le mot de passe doit contenir au moins " + LONGUEUR_MIN_MOT_DE_PASSE + " caractères.");
        }

        String motDePasseHache = AuthService.hasher(motDePasse);
        Utilisateur utilisateur = new Utilisateur(0, nom, prenom, email, motDePasseHache, true, role);
        return utilisateurRepository.save(utilisateur);
    }

    public void modifierUtilisateur(int id, String nom, String prenom, String email) {
        Utilisateur utilisateur = trouverOuLever(id);

        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        utilisateur.setEmail(email);
        utilisateurRepository.update(utilisateur);
    }

    public void supprimerUtilisateur(int id) {
        utilisateurRepository.deleteById(id);
    }

    public void activer(int id) {
        Utilisateur utilisateur = trouverOuLever(id);
        utilisateur.setActif(true);
        utilisateurRepository.update(utilisateur);
    }

    public void desactiver(int id) {
        Utilisateur utilisateur = trouverOuLever(id);
        utilisateur.setActif(false);
        utilisateurRepository.update(utilisateur);
    }

    public void changerRole(int id, Role nouveauRole) {
        Utilisateur utilisateur = trouverOuLever(id);
        utilisateur.setRole(nouveauRole);
        utilisateurRepository.update(utilisateur);
    }

    private Utilisateur trouverOuLever(int id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable avec l'id " + id));
    }
}