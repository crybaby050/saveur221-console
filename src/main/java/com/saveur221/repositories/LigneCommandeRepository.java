package com.saveur221.repositories;

import com.saveur221.config.DatabaseConnection;
import com.saveur221.entities.LigneCommande;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository dédié à l'accès aux données des lignes de commande.
 */
public class LigneCommandeRepository implements Repository<LigneCommande, Integer> {

    @Override
    public Optional<LigneCommande> findById(Integer id) {
        String sql = "SELECT id, commande_id, produit_id, quantite, prix_unitaire " +
                     "FROM ligne_commandes WHERE id = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToEntity(rs));
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la recherche de la ligne de commande : "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public List<LigneCommande> findAll() {
        String sql =
                "SELECT id, commande_id, produit_id, quantite, prix_unitaire " +
                "FROM ligne_commandes";

        List<LigneCommande> lignes = new ArrayList<>();

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                lignes.add(mapToEntity(rs));
            }

            return lignes;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la récupération des lignes de commande : "
                            + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Récupère les lignes associées à une commande.
     *
     * @param commandeId identifiant de la commande
     * @return liste des lignes de la commande
     */
    public List<LigneCommande> findByCommandeId(int commandeId) {
        String sql =
                "SELECT id, commande_id, produit_id, quantite, prix_unitaire " +
                "FROM ligne_commandes WHERE commande_id = ?";

        List<LigneCommande> lignes = new ArrayList<>();

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, commandeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lignes.add(mapToEntity(rs));
                }

                return lignes;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la récupération des lignes de la commande : "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public LigneCommande save(LigneCommande entite) {
        String sql =
                "INSERT INTO ligne_commandes " +
                "(commande_id, produit_id, quantite, prix_unitaire) " +
                "VALUES (?, ?, ?, ?)";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {
            stmt.setInt(1, entite.getCommandeId());
            stmt.setInt(2, entite.getProduitId());
            stmt.setInt(3, entite.getQuantite());
            stmt.setDouble(4, entite.getPrixUnitaire());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    entite.setId(keys.getInt(1));
                }
            }

            return entite;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la création de la ligne de commande : "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public void update(LigneCommande entite) {
        String sql =
                "UPDATE ligne_commandes " +
                "SET quantite = ?, prix_unitaire = ? " +
                "WHERE id = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, entite.getQuantite());
            stmt.setDouble(2, entite.getPrixUnitaire());
            stmt.setInt(3, entite.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la modification de la ligne de commande : "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM ligne_commandes WHERE id = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la suppression de la ligne de commande : "
                            + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Convertit une ligne SQL en entité LigneCommande.
     *
     * @param rs résultat SQL contenant les données de la ligne
     * @return ligne de commande correspondante
     * @throws SQLException en cas d'erreur de lecture du résultat
     */
    private LigneCommande mapToEntity(ResultSet rs) throws SQLException {
        return new LigneCommande(
                rs.getInt("id"),
                rs.getInt("commande_id"),
                rs.getInt("produit_id"),
                rs.getInt("quantite"),
                rs.getDouble("prix_unitaire")
        );
    }
}