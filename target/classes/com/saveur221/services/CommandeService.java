package com.saveur221.services;

import com.saveur221.entities.Commande;
import com.saveur221.entities.LigneCommande;
import com.saveur221.entities.Produit;
import com.saveur221.enums.StatutCommande;
import com.saveur221.enums.StatutPaiement;
import com.saveur221.exceptions.CommandeInexistanteException;
import com.saveur221.exceptions.CommandeInvalideException;
import com.saveur221.exceptions.ProduitInexistantException;
import com.saveur221.exceptions.StockInsuffisantException;
import com.saveur221.exceptions.TransitionStatutInvalideException;
import com.saveur221.repositories.CommandeRepository;
import com.saveur221.repositories.LigneCommandeRepository;
import com.saveur221.repositories.ProduitRepository;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;

public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final LigneCommandeRepository ligneCommandeRepository;
    private final ProduitRepository produitRepository;
    private final ProduitService produitService;
    private final FactureService factureService;

    public CommandeService(CommandeRepository commandeRepository,
                            LigneCommandeRepository ligneCommandeRepository,
                            ProduitRepository produitRepository,
                            ProduitService produitService,
                            FactureService factureService) {
        this.commandeRepository = commandeRepository;
        this.ligneCommandeRepository = ligneCommandeRepository;
        this.produitRepository = produitRepository;
        this.produitService = produitService;
        this.factureService = factureService;
    }

    public List<Commande> listerCommandes() {
        return commandeRepository.findAll();
    }

    public List<Commande> filtrerParStatut(StatutCommande statut) {
        return commandeRepository.findByStatut(statut);
    }

    public Commande rechercherParNumero(String numeroCommande) {
        return commandeRepository.findByNumeroCommande(numeroCommande)
                .orElseThrow(() -> new CommandeInexistanteException("Aucune commande avec le numéro " + numeroCommande));
    }

    // US "Enregistrer une commande sur place" : vente au comptoir, saisie
    // directement par le Gérant. La map associe un produitId à la quantité
    // vendue — c'est la couche vue (menu console) qui construit cette map
    // au fil des saisies de l'utilisateur.
    public Commande creerCommandeSurPlace(int clientId, Map<Integer, Integer> lignesSaisies,
                                           StatutCommande statutInitial) {
        if (lignesSaisies.isEmpty()) {
            throw new CommandeInvalideException("Une commande doit contenir au moins un article.");
        }

        Commande commande = new Commande(0, genererNumeroCommande(), clientId, LocalDateTime.now(),
                statutInitial, StatutPaiement.IMPAYE, 0.0);
        commande = commandeRepository.save(commande);

        double montantTotal = 0.0;

        for (Map.Entry<Integer, Integer> entree : lignesSaisies.entrySet()) {
            int produitId = entree.getKey();
            int quantite = entree.getValue();

            Produit produit = produitRepository.findById(produitId)
                    .orElseThrow(() -> new ProduitInexistantException("Produit introuvable avec l'id " + produitId));

            // Règle métier : impossible de commander plus que la quantité disponible.
            if (quantite > produit.getQuantiteStock()) {
                throw new StockInsuffisantException(
                        "Stock insuffisant pour " + produit.getLibelle() + " (disponible : " + produit.getQuantiteStock() + ")");
            }

            LigneCommande ligne = new LigneCommande(0, commande.getId(), produitId, quantite, produit.getPrix());
            ligneCommandeRepository.save(ligne);

            // Le stock diminue immédiatement après la vente.
            produitService.diminuerStock(produitId, quantite);

            montantTotal += ligne.calculerSousTotal();
        }

        commande.setMontantTotal(montantTotal);
        commandeRepository.update(commande);

        // La facture est générée automatiquement, sans action supplémentaire
        // du Gérant — voir FactureService.
        factureService.genererFacture(commande);

        return commande;
    }

    public void changerStatut(int commandeId, StatutCommande nouveauStatut) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new CommandeInexistanteException("Commande introuvable avec l'id " + commandeId));

        if (nouveauStatut == StatutCommande.ANNULEE) {
            // L'annulation est acceptée depuis n'importe quel statut — pas
            // besoin de vérifier une transition particulière ici.
            restaurerStockDeLaCommande(commandeId);
        } else {
            verifierTransitionValide(commande.getStatut(), nouveauStatut);
        }

        commande.setStatut(nouveauStatut);
        commandeRepository.update(commande);
    }

    private void restaurerStockDeLaCommande(int commandeId) {
        List<LigneCommande> lignes = ligneCommandeRepository.findByCommandeId(commandeId);

        for (LigneCommande ligne : lignes) {
            produitService.restaurerStock(ligne.getProduitId(), ligne.getQuantite());
        }
    }

    // Seul l'enchaînement EN_ATTENTE → EN_PREPARATION → PRETE → RETIREE est
    // autorisé pour une transition "normale" (hors annulation, traitée à part).
    private void verifierTransitionValide(StatutCommande statutActuel, StatutCommande nouveauStatut) {
        boolean valide = switch (statutActuel) {
            case EN_ATTENTE -> nouveauStatut == StatutCommande.EN_PREPARATION;
            case EN_PREPARATION -> nouveauStatut == StatutCommande.PRETE;
            case PRETE -> nouveauStatut == StatutCommande.RETIREE;
            case RETIREE, ANNULEE -> false; // aucun changement possible après ces statuts
        };

        if (!valide) {
            throw new TransitionStatutInvalideException(
                    "Transition impossible de " + statutActuel + " vers " + nouveauStatut);
        }
    }

    // Numéro lisible du type CMD-2026-000231 — même logique de compteur
    // simple que pour les factures et reçus (voir FactureService, RecuService).
    private String genererNumeroCommande() {
        int nombreCommandes = commandeRepository.findAll().size();
        int annee = Year.now().getValue();
        return "CMD-" + annee + "-" + String.format("%06d", nombreCommandes + 1);
    }
}