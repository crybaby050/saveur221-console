package com.saveur221.repositories;

import com.saveur221.config.DatabaseConnection;
import com.saveur221.entities.Categorie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategorieRepository implements Repository<Categorie, Integer> {

    @Override
    public Optional<Categorie> findById(Integer id) {
        String sql = "SELECT id, nom, description FROM categories WHERE id = ?";

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
            throw new RuntimeException("Erreur lors de la recherche de la catégorie : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Categorie> findAll() {
        String sql = "SELECT id, nom, description FROM categories ORDER BY nom";
        List<Categorie> categories = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(mapToEntity(rs));
            }
            return categories;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des catégories : " + e.getMessage(), e);
        }
    }

    // Retourne une catégorie par son nom exact — utilisée par CategorieService
    // pour vérifier l'unicité avant création, et pour l'US "Rechercher une
    // catégorie".
    public List<Categorie> rechercherParNom(String motCle) {
        String sql = "SELECT id, nom, description FROM categories WHERE nom ILIKE ? ORDER BY nom";
        List<Categorie> categories = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // ILIKE : comparaison insensible à la casse, propre à PostgreSQL.
            stmt.setString(1, "%" + motCle + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapToEntity(rs));
                }
                return categories;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de catégories : " + e.getMessage(), e);
        }
    }

    // Utilisée par CategorieService pour appliquer la règle métier : une
    // catégorie contenant des produits ne peut pas être supprimée.
    public boolean contientDesProduits(int categorieId) {
        String sql = "SELECT COUNT(*) AS total FROM produits WHERE categorie_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categorieId);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt("total") > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification des produits liés : " + e.getMessage(), e);
        }
    }

    @Override
    public Categorie save(Categorie entite) {
        String sql = "INSERT INTO categories (nom, description) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, entite.getNom());
            stmt.setString(2, entite.getDescription());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    entite.setId(keys.getInt(1));
                }
            }
            return entite;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la catégorie : " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Categorie entite) {
        String sql = "UPDATE categories SET nom = ?, description = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entite.getNom());
            stmt.setString(2, entite.getDescription());
            stmt.setInt(3, entite.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification de la catégorie : " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM categories WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la catégorie : " + e.getMessage(), e);
        }
    }

    // Reconstruit une Categorie à partir d'une ligne de résultat SQL —
    // centralisé ici pour ne pas dupliquer le mapping dans chaque méthode.
    private Categorie mapToEntity(ResultSet rs) throws SQLException {
        return new Categorie(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("description"));
    }
}