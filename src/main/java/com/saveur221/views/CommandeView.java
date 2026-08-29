package com.saveur221.views;

import com.saveur221.entities.Client;
import com.saveur221.entities.Commande;
import com.saveur221.entities.Facture;
import com.saveur221.entities.Produit;
import com.saveur221.enums.StatutCommande;
import com.saveur221.exceptions.ClientInexistantException;
import com.saveur221.exceptions.CommandeInexistanteException;
import com.saveur221.exceptions.CommandeInvalideException;
import com.saveur221.exceptions.ProduitInexistantException;
import com.saveur221.exceptions.StockInsuffisantException;
import com.saveur221.exceptions.TransitionStatutInvalideException;
import com.saveur221.services.ClientService;
import com.saveur221.services.CommandeService;
import com.saveur221.services.FactureService;
import com.saveur221.services.ProduitService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * Menu de gestion des commandes, accessible au Gérant.
 * Regroupe les US14 à US18 du Sprint 3 (consultation, recherche, filtrage,
 * changement de statut, annulation) ainsi que le cas d'utilisation bonus
 * "Enregistrer une commande sur place", qui déclenche automatiquement la
 * génération de la facture correspondante (voir FactureService).
 */
public class CommandeView extends MenuView {

    private final CommandeService commandeService;
    private final ClientService clientService;
    private final ProduitService produitService;
    private final FactureService factureService;

    public CommandeView(CommandeService commandeService, ClientService clientService,
                         ProduitService produitService, FactureService factureService, Scanner scanner) {
        super(scanner);
        this.commandeService = commandeService;
        this.clientService = clientService;
        this.produitService = produitService;
        this.factureService = factureService;
    }

    @Override
    protected void afficherOptions() {
        System.out.println();
        System.out.println("=== Gestion des commandes ===");
        System.out.println("1. Enregistrer une commande sur place");
        System.out.println("2. Consulter les commandes");
        System.out.println("3. Rechercher une commande (par numéro)");
        System.out.println("4. Filtrer les commandes par statut");
        System.out.println("5. Changer le statut d'une commande");
        System.out.println("6. Annuler une commande");
        System.out.println("0. Retour au menu principal");
    }

    @Override
    protected boolean traiterChoix(int choix) {
        try {
            switch (choix) {
                case 1 -> enregistrerCommandeSurPlace();
                case 2 -> consulterCommandes();
                case 3 -> rechercherParNumero();
                case 4 -> filtrerParStatut();
                case 5 -> changerStatut();
                case 6 -> annulerCommande();
                case 0 -> {
                    return false;
                }
                default -> System.out.println("Choix invalide, réessayez.");
            }
        } catch (ClientInexistantException | ProduitInexistantException | StockInsuffisantException
                 | CommandeInvalideException | CommandeInexistanteException
                 | TransitionStatutInvalideException e) {
            // Toutes les erreurs métier prévisibles de ce menu sont attrapées
            // ici, au même endroit, plutôt que dans chaque méthode privée —
            // évite de dupliquer huit fois le même bloc try/catch.
            System.out.println("Erreur : " + e.getMessage());
        }
        return true;
    }

    private void enregistrerCommandeSurPlace() {
        System.out.println();
        System.out.println("--- Enregistrer une commande sur place ---");

        System.out.print("Email du client : ");
        String email = scanner.nextLine();
        Client client = clientService.rechercherParEmail(email);
        System.out.println("Client trouvé : " + client.getNom() + " " + client.getPrenom());

        Map<Integer, Integer> lignesSaisies = saisirLignesDeCommande();
        if (lignesSaisies.isEmpty()) {
            System.out.println("Enregistrement annulé : aucune ligne saisie.");
            return;
        }

        StatutCommande statutInitial = choisirStatutRemise();

        Commande commande = commandeService.creerCommandeSurPlace(client.getId(), lignesSaisies, statutInitial);

        System.out.println("Commande enregistrée avec succès :");
        System.out.println(commande.toChaine());

        // La facture a été générée automatiquement par CommandeService — on
        // la récupère juste pour confirmer son numéro à l'utilisateur.
        Optional<Facture> facture = factureService.consulterParCommande(commande.getId());
        facture.ifPresent(f -> System.out.println("Facture générée : " + f.getNumeroFacture()));
    }

    // Boucle de saisie des lignes : affiche les produits disponibles, puis
    // demande id + quantité jusqu'à ce que l'utilisateur tape 0 pour arrêter.
    private Map<Integer, Integer> saisirLignesDeCommande() {
        Map<Integer, Integer> lignes = new HashMap<>();

        List<Produit> produits = produitService.listerProduits();
        System.out.println("Produits disponibles :");
        for (Produit produit : produits) {
            System.out.println(produit.getId() + " - " + produit.getLibelle() +
                    " (stock : " + produit.getQuantiteStock() + ", prix : " + produit.getPrix() + ")");
        }

        while (true) {
            int produitId = lireEntier("Id du produit à ajouter (0 pour terminer) : ");
            if (produitId == 0) {
                break;
            }

            int quantite = lireEntier("Quantité : ");
            lignes.put(produitId, quantite);
            System.out.println("Ligne ajoutée. Ajoutez un autre produit ou tapez 0 pour terminer.");
        }

        return lignes;
    }

    // Une vente au comptoir est presque toujours remise immédiatement au
    // client — RETIREE est donc la valeur par défaut si l'utilisateur ne
    // saisit rien, conformément à ce qu'on avait décidé ensemble.
    private StatutCommande choisirStatutRemise() {
        System.out.println("La commande est-elle déjà remise au client ?");
        System.out.println("1. Oui, retirée (par défaut)");
        System.out.println("2. Non, prête mais pas encore retirée");

        System.out.print("Votre choix (Entrée = 1) : ");
        String saisie = scanner.nextLine().trim();

        if (saisie.equals("2")) {
            return StatutCommande.PRETE;
        }
        return StatutCommande.RETIREE;
    }

    private void consulterCommandes() {
        System.out.println();
        List<Commande> commandes = commandeService.listerCommandes();
        afficherListeCommandes(commandes);
    }

    private void rechercherParNumero() {
        System.out.println();
        System.out.print("Numéro de commande : ");
        String numero = scanner.nextLine();

        Commande commande = commandeService.rechercherParNumero(numero);
        System.out.println(commande.toChaine());
    }

    private void filtrerParStatut() {
        System.out.println();
        StatutCommande statut = choisirStatut();
        if (statut == null) {
            System.out.println("Filtrage annulé.");
            return;
        }

        List<Commande> resultats = commandeService.filtrerParStatut(statut);
        afficherListeCommandes(resultats);
    }

    private void changerStatut() {
        System.out.println();
        int id = lireEntier("Id de la commande : ");

        StatutCommande nouveauStatut = choisirStatut();
        if (nouveauStatut == null) {
            System.out.println("Changement annulé.");
            return;
        }

        commandeService.changerStatut(id, nouveauStatut);
        System.out.println("Statut mis à jour avec succès.");
    }

    private void annulerCommande() {
        System.out.println();
        int id = lireEntier("Id de la commande à annuler : ");

        // Le stock est restauré automatiquement par CommandeService — cette
        // vue n'a rien de plus à faire que d'appeler le changement de statut.
        commandeService.changerStatut(id, StatutCommande.ANNULEE);
        System.out.println("Commande annulée, stock restitué.");
    }

    // Affiche les 5 statuts possibles sous forme de menu numéroté, plutôt
    // que de demander à l'utilisateur de taper le nom exact de l'enum.
    private StatutCommande choisirStatut() {
        StatutCommande[] statuts = StatutCommande.values();

        System.out.println("Statuts disponibles :");
        for (int i = 0; i < statuts.length; i++) {
            System.out.println((i + 1) + ". " + statuts[i]);
        }

        int choix = lireEntier("Votre choix : ");
        if (choix < 1 || choix > statuts.length) {
            return null;
        }
        return statuts[choix - 1];
    }

    private void afficherListeCommandes(List<Commande> commandes) {
        if (commandes.isEmpty()) {
            System.out.println("Aucune commande à afficher.");
            return;
        }

        for (Commande commande : commandes) {
            System.out.println(commande.toChaine());
        }
    }

}