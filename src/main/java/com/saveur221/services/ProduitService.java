package com.saveur221.services;

import com.saveur221.entities.Produit;
import com.saveur221.exceptions.ProduitInexistantException;
import com.saveur221.repositories.ProduitRepository;

import java.util.List;

public class ProduitService {

    private final ProduitRepository produitRepository;

    public ProduitService(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    public List<Produit> listerProduits() {
        return produitRepository.findAll();
    }

    public List<Produit> rechercherProduit(String motCle) {
        return produitRepository.rechercherParLibelle(motCle);
    }

    public List<Produit> filtrerParCategorie(int categorieId) {
        return produitRepository.findByCategorieId(categorieId);
    }

    public List<Produit> consulterStockFaible() {
        return produitRepository.findStockFaible();
    }

    public List<Produit> consulterRuptures() {
        return produitRepository.findEnRupture();
    }

    public Produit ajouterProduit(String libelle, String description, double prix,
            int quantiteStock, int seuilAlerte, int categorieId) {
        // "image" est toujours null à la création côté Java — c'est le
        // repository qui l'impose au niveau SQL, pas ce service.
        boolean disponible = quantiteStock > 0;
        Produit produit = new Produit(0, libelle, description, prix, quantiteStock,
                seuilAlerte, disponible, null, categorieId);
        return produitRepository.save(produit);
    }

    public void modifierProduit(int id, String libelle, String description, double prix, int categorieId) {
        Produit produit = trouverOuLever(id);

        produit.setLibelle(libelle);
        produit.setDescription(description);
        produit.setPrix(prix);
        produit.setCategorieId(categorieId);
        produitRepository.update(produit);
    }

    public void supprimerProduit(int id) {
        produitRepository.deleteById(id);
    }

    public void approvisionner(int produitId, int quantite) {
        Produit produit = trouverOuLever(produitId);

        // La logique de recalcul de disponibilité vit dans l'entité elle-même
        // (Produit.approvisionner) — le service orchestre, l'entité applique
        // sa propre règle métier sur ses données.
        produit.approvisionner(quantite);
        produitRepository.update(produit);
    }

    public void definirSeuilAlerte(int produitId, int seuil) {
        Produit produit = trouverOuLever(produitId);
        produit.setSeuilAlerte(seuil);
        produitRepository.update(produit);
    }

    // Utilisée en interne par CommandeService lors de la création d'une
    // commande : diminue le stock et persiste immédiatement le changement.
    public void diminuerStock(int produitId, int quantite) {
        Produit produit = trouverOuLever(produitId);
        produit.diminuerStock(quantite);
        produitRepository.update(produit);
    }

    // Utilisée en interne par CommandeService lors de l'annulation d'une
    // commande : restitue le stock des produits concernés.
    public void restaurerStock(int produitId, int quantite) {
        Produit produit = trouverOuLever(produitId);
        produit.restaurerStock(quantite);
        produitRepository.update(produit);
    }

    public Produit consulterProduit(int id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new ProduitInexistantException("Produit introuvable avec l'id " + id));
    }

    private Produit trouverOuLever(int id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new ProduitInexistantException("Produit introuvable avec l'id " + id));
    }
}