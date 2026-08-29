package com.saveur221.enums;

/**
 * Statut de paiement d'une commande, indépendant de son statut de
 * préparation ({@link StatutCommande}).
 *
 * <p>Une commande naît toujours à {@link #IMPAYE} : rien n'oblige à
 * encaisser un paiement au moment de sa création. Elle passe à
 * {@link #PARTIEL} dès le premier paiement enregistré si un solde reste dû,
 * puis à {@link #PAYEE} lorsque la somme des paiements couvre exactement le
 * montant total. Cette progression est pilotée par
 * {@code PaiementService}, jamais modifiée manuellement.</p>
 */
public enum StatutPaiement {
    IMPAYE,
    PARTIEL,
    PAYEE
}