package com.saveur221.repositories;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {

    // Recherche une entité par son identifiant. Optional plutôt que null :
    // oblige l'appelant (le service) à gérer explicitement le cas "absent",
    // par exemple en levant une UtilisateurInexistantException.
    Optional<T> findById(ID id);

    // Retourne toutes les entités de ce type — utilisé par les US de type
    // "Consulter les produits", "Consulter les commandes", etc.
    List<T> findAll();

    // Insère une nouvelle entité en base et retourne l'entité persistée
    // (avec son id généré renseigné).
    T save(T entite);

    // Met à jour une entité déjà existante en base (identifiée par son id).
    void update(T entite);

    // Supprime une entité par son identifiant.
    void deleteById(ID id);
}