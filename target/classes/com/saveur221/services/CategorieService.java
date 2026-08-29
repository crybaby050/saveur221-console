package com.saveur221.services;

import com.saveur221.entities.Categorie;
import com.saveur221.exceptions.CategorieUtiliseeException;
import com.saveur221.repositories.CategorieRepository;
import com.saveur221.repositories.ProduitRepository;

import java.util.List;
import java.util.Optional;

public class CategorieService {

    // Les deux repositories sont injectés par le constructeur plutôt que
    // créés ici avec "new" — c'est le principe d'injection de dépendance :
    // ce service ne sait pas COMMENT les données sont lues/écrites, juste
    // QUI le fait pour lui. Ça permettra plus tard de brancher un conteneur
    // IoC ou des repositories de test (mocks) sans toucher à cette classe.
    private final CategorieRepository categorieRepository;
    private final ProduitRepository produitRepository;

    public CategorieService(CategorieRepository categorieRepository, ProduitRepository produitRepository) {
        this.categorieRepository = categorieRepository;
        this.produitRepository = produitRepository;
    }

    public List<Categorie> listerCategories() {
        return categorieRepository.findAll();
    }

    public List<Categorie> rechercherCategorie(String motCle) {
        return categorieRepository.rechercherParNom(motCle);
    }

    public Categorie ajouterCategorie(String nom, String description) {
        Categorie categorie = new Categorie(0, nom, description);
        return categorieRepository.save(categorie);
    }

    public void modifierCategorie(int id, String nom, String description) {
        Optional<Categorie> resultat = categorieRepository.findById(id);
        Categorie categorie = resultat.orElseThrow(() ->
                new IllegalArgumentException("Catégorie introuvable avec l'id " + id));

        categorie.setNom(nom);
        categorie.setDescription(description);
        categorieRepository.update(categorie);
    }

    public void supprimerCategorie(int id) {
        // Règle métier : une catégorie contenant des produits ne peut pas
        // être supprimée. La vérification passe par ProduitRepository
        // plutôt que par une requête directe dans CategorieRepository —
        // chaque repository reste responsable d'une seule table.
        boolean contientDesProduits = !produitRepository.findByCategorieId(id).isEmpty();

        if (contientDesProduits) {
            throw new CategorieUtiliseeException(
                    "Impossible de supprimer cette catégorie : elle contient encore des produits.");
        }

        categorieRepository.deleteById(id);
    }
}