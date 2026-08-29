package com.saveur221.enums;

/**
 * Statut d'une commande, reflétant son avancement dans le cycle de
 * préparation du restaurant.
 *
 * <p>Correspond strictement au type PostgreSQL {@code statut_commande}
 * défini dans le script SQL. Le nom de chaque constante ({@link #name()})
 * doit être identique à la valeur stockée en base : PostgreSQL est strict
 * sur la casse et rejette toute valeur ne figurant pas dans le type ENUM.</p>
 *
 * <p>Transitions autorisées dans le cas normal :
 * {@code EN_ATTENTE → EN_PREPARATION → PRETE → RETIREE}.
 * {@link #ANNULEE} peut en revanche être atteint depuis n'importe quel
 * statut précédent — cette règle est vérifiée dans {@code CommandeService},
 * pas dans l'enum lui-même.</p>
 */
public enum StatutCommande {
    EN_ATTENTE,
    EN_PREPARATION,
    PRETE,
    RETIREE,
    ANNULEE
}