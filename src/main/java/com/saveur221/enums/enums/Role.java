package com.saveur221.enums;

/**
 * Rôle attribué à un utilisateur interne (personnel du restaurant).
 *
 * <p>Correspond strictement à la colonne {@code nom} de la table
 * {@code roles} en base de données. Toute modification ici doit être
 * répercutée dans le script SQL, et inversement.</p>
 */
public enum Role {
    ADMIN,
    GERANT
}