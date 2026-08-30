package com.saveur221.repositories;

import java.util.List;
import java.util.Optional;

/**
 * Contrat générique définissant les opérations CRUD de base
 * applicables aux repositories de l'application.
 *
 * @param <T> type de l'entité gérée
 * @param <ID> type de l'identifiant de l'entité
 */
public interface Repository<T, ID> {

    /**
     * Recherche une entité à partir de son identifiant.
     *
     * <p>
     * L'utilisation de {@link Optional} permet de représenter explicitement
     * le cas où aucune entité ne correspond à l'identifiant fourni.
     * </p>
     *
     * @param id identifiant de l'entité recherchée
     * @return l'entité correspondante si elle existe, sinon un {@link Optional} vide
     */
    Optional<T> findById(ID id);

    /**
     * Récupère l'ensemble des entités gérées par le repository.
     *
     * @return liste des entités
     */
    List<T> findAll();

    /**
     * Enregistre une nouvelle entité en base de données.
     *
     * <p>
     * L'entité retournée peut notamment contenir les valeurs générées
     * par la base de données, comme son identifiant.
     * </p>
     *
     * @param entite entité à enregistrer
     * @return entité persistée
     */
    T save(T entite);

    /**
     * Met à jour une entité existante en base de données.
     *
     * @param entite entité contenant les nouvelles valeurs
     */
    void update(T entite);

    /**
     * Supprime une entité à partir de son identifiant.
     *
     * @param id identifiant de l'entité à supprimer
     */
    void deleteById(ID id);
}
