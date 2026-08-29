package com.saveur221.enums;

/**
 * Nature d'un paiement au moment où son reçu est émis.
 *
 * <p>Déterminé par {@code RecuService} en comparant, au moment de
 * l'enregistrement du paiement, le total déjà payé (paiement courant
 * inclus) au montant total de la commande : {@link #TOTAL} si le solde
 * restant tombe à zéro, {@link #PARTIEL} sinon. Une fois émis, un reçu ne
 * change jamais de type, même si un paiement ultérieur solde la
 * commande — c'est le reçu suivant qui portera la mention {@link #TOTAL}.</p>
 */
public enum TypePaiementRecu {
    PARTIEL,
    TOTAL
}