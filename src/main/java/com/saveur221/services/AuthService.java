package com.saveur221.services;

import com.saveur221.entities.Utilisateur;
import com.saveur221.exceptions.CompteDesactiveException;
import com.saveur221.exceptions.MotDePasseIncorrectException;
import com.saveur221.exceptions.UtilisateurInexistantException;
import com.saveur221.repositories.UtilisateurRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {

    private final UtilisateurRepository utilisateurRepository;

    public AuthService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public Utilisateur authentifier(String email, String motDePasse) {
        Optional<Utilisateur> resultat = utilisateurRepository.findByEmail(email);

        Utilisateur utilisateur = resultat
                .orElseThrow(() -> new UtilisateurInexistantException("Aucun utilisateur trouvé avec cet email."));

        // BCrypt.checkpw compare le mot de passe en clair au hash stocké —
        // le sel est intégré dans le hash lui-même, pas besoin de le gérer séparément.
        if (!BCrypt.checkpw(motDePasse, utilisateur.getMotDePasse())) {
            throw new MotDePasseIncorrectException("Mot de passe incorrect.");
        }

        if (!utilisateur.isActif()) {
            throw new CompteDesactiveException("Ce compte a été désactivé.");
        }

        return utilisateur;
    }

    // Utilisée par UtilisateurService à la création d'un compte, et par tout
    // script d'initialisation de données (premier admin, jeux de test).
    public static String hasher(String motDePasse) {
        // Le "cost factor" par défaut de jBCrypt (10) est un bon compromis
        // sécurité/performance pour ce projet — pas besoin de le paramétrer.
        return BCrypt.hashpw(motDePasse, BCrypt.gensalt());
    }
}