package com.saveur221.repositories;

import java.util.List;
import java.util.Optional;

/**
 * Contrat de lecture générique pour l'accès aux entités.
 *
 * @param <T> type de l'entité
 * @param <ID> type de l'identifiant
 */
public interface LectureSeuleRepository<T, ID> {

    Optional<T> findById(ID id);

    List<T> findAll();
}