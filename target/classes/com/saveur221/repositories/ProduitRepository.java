package com.saveur221.repositories;

import com.saveur221.config.DatabaseConnection;
import com.saveur221.entities.Produit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProduitRepository implements Repository<Produit, Integer> {

    @Override
    public Optional<Produit> findById(Integer id) {
        String sql = "SELECT id, libelle, description, prix, quantite_stock, seuil_alerte, " +
                "disponible, image, categorie_id FROM produits WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToEntity(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du produit : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Produit> findAll() {
        String sql = "SELECT id, libelle, description, prix, quantite_stock, seuil_alerte, " +
                "disponible, image, categorie_id FROM produits ORDER BY libelle";
        List<Produit> produits = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                produits.add(mapToEntity(rs));
            }
            return produits;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des produits : " + e.getMessage(), e);
        }
    }

    // US "Filtrer les produits par catégorie".
    public List<Produit> findByCategorieId(int categorieId) {
        String sql = "SELECT id, libelle, description, prix, quantite_stock, seuil_alerte, " +
                "disponible, image, categorie_id FROM produits WHERE categorie_id = ? ORDER BY libelle";
        List<Produit> produits = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categorieId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produits.add(mapToEntity(rs));
                }
                return produits;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du filtrage des produits : " + e.getMessage(), e);
        }
    }

    // US "Rechercher un produit".
    public List<Produit> rechercherParLibelle(String motCle) {
        String sql = "SELECT id, libelle, description, prix, quantite_stock, seuil_alerte, " +
                "disponible, image, categorie_id FROM produits WHERE libelle ILIKE ? ORDER BY libelle";
        List<Produit> produits = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + motCle + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produits.add(mapToEntity(rs));
                }
                return produits;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de produits : " + e.getMessage(), e);
        }
    }

    // US "Consulter le stock" : produits sous leur seuil d'alerte (mais pas
    // encore en rupture totale).
    public List<Produit> findStockFaible() {
        String sql = "SELECT id, libelle, description, prix, quantite_stock, seuil_alerte, " +
                "disponible, image, categorie_id FROM produits " +
                "WHERE quantite_stock > 0 AND quantite_stock <= seuil_alerte ORDER BY quantite_stock";
        List<Produit> produits = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                produits.add(mapToEntity(rs));
            }
            return produits;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la récupération des produits en stock faible : " + e.getMessage(), e);
        }
    }

    // US "Consulter le stock" : produits en rupture complète.
    public List<Produit> findEnRupture() {
        String sql = "SELECT id, libelle, description, prix, quantite_stock, seuil_alerte, " +
                "disponible, image, categorie_id FROM produits WHERE quantite_stock = 0 ORDER BY libelle";
        List<Produit> produits = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                produits.add(mapToEntity(rs));
            }
            return produits;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des produits en rupture : " + e.getMessage(), e);
        }
    }

    @Override
    public Produit save(Produit entite) {
        // "image" n'est jamais renseignée depuis le Java Console : toujours
        // écrite à NULL, quelle que soit la valeur portée par l'entité.
        String sql = "INSERT INTO produits (libelle, description, prix, quantite_stock, " +
                "seuil_alerte, disponible, image, categorie_id) VALUES (?, ?, ?, ?, ?, ?, NULL, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, entite.getLibelle());
            stmt.setString(2, entite.getDescription());
            stmt.setDouble(3, entite.getPrix());
            stmt.setInt(4, entite.getQuantiteStock());
            stmt.setInt(5, entite.getSeuilAlerte());
            stmt.setBoolean(6, entite.isDisponible());
            stmt.setInt(7, entite.getCategorieId());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    entite.setId(keys.getInt(1));
                }
            }
            return entite;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du produit : " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Produit entite) {
        // La colonne "image" n'est volontairement pas incluse dans ce
        // UPDATE : le Java Console ne doit jamais l'écraser, même par erreur.
        String sql = "UPDATE produits SET libelle = ?, description = ?, prix = ?, quantite_stock = ?, " +
                "seuil_alerte = ?, disponible = ?, categorie_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entite.getLibelle());
            stmt.setString(2, entite.getDescription());
            stmt.setDouble(3, entite.getPrix());
            stmt.setInt(4, entite.getQuantiteStock());
            stmt.setInt(5, entite.getSeuilAlerte());
            stmt.setBoolean(6, entite.isDisponible());
            stmt.setInt(7, entite.getCategorieId());
            stmt.setInt(8, entite.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification du produit : " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM produits WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du produit : " + e.getMessage(), e);
        }
    }

    private Produit mapToEntity(ResultSet rs) throws SQLException {
        return new Produit(
                rs.getInt("id"),
                rs.getString("libelle"),
                rs.getString("description"),
                rs.getDouble("prix"),
                rs.getInt("quantite_stock"),
                rs.getInt("seuil_alerte"),
                rs.getBoolean("disponible"),
                rs.getString("image"), // toujours null en pratique côté Java
                rs.getInt("categorie_id"));
    }
}