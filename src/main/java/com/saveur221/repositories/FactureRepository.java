package com.saveur221.repositories;

import com.saveur221.config.DatabaseConnection;
import com.saveur221.entities.Facture;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository dédié à l'accès aux données des factures.
 */
public class FactureRepository implements Repository<Facture, Integer> {

    @Override
    public Optional<Facture> findById(Integer id) {
        String sql = "SELECT id, numero_facture, commande_id, montant_total, date_emission " +
                     "FROM factures WHERE id = ?";

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
                    "Erreur lors de la recherche de la facture : " + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public List<Facture> findAll() {
        String sql = "SELECT id, numero_facture, commande_id, montant_total, date_emission " +
                     "FROM factures ORDER BY date_emission DESC";

        List<Facture> factures = new ArrayList<>();

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                factures.add(mapToEntity(rs));
            }

            return factures;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la récupération des factures : "
                            + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Recherche la facture associée à une commande.
     *
     * @param commandeId identifiant de la commande
     * @return facture associée si elle existe
     */
    public Optional<Facture> findByCommandeId(int commandeId) {
        String sql = "SELECT id, numero_facture, commande_id, montant_total, date_emission " +
                     "FROM factures WHERE commande_id = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, commandeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToEntity(rs));
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la recherche de la facture de la commande : "
                            + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Recherche une facture à partir de son numéro.
     *
     * @param numeroFacture numéro de la facture
     * @return facture correspondante si elle existe
     */
    public Optional<Facture> findByNumeroFacture(String numeroFacture) {
        String sql = "SELECT id, numero_facture, commande_id, montant_total, date_emission " +
                     "FROM factures WHERE numero_facture = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, numeroFacture);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToEntity(rs));
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la recherche de la facture par numéro : "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public Facture save(Facture entite) {
        String sql =
                "INSERT INTO factures " +
                "(numero_facture, commande_id, montant_total, date_emission) " +
                "VALUES (?, ?, ?, ?)";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {
            stmt.setString(1, entite.getNumeroFacture());
            stmt.setInt(2, entite.getCommandeId());
            stmt.setDouble(3, entite.getMontantTotal());
            stmt.setTimestamp(
                    4,
                    Timestamp.valueOf(entite.getDateEmission())
            );

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    entite.setId(keys.getInt(1));
                }
            }

            return entite;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la création de la facture : "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public void update(Facture entite) {
        String sql = "UPDATE factures SET montant_total = ? WHERE id = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setDouble(1, entite.getMontantTotal());
            stmt.setInt(2, entite.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la modification de la facture : "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM factures WHERE id = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la suppression de la facture : "
                            + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Convertit une ligne SQL en entité Facture.
     *
     * @param rs résultat SQL contenant les données de la facture
     * @return facture correspondante
     * @throws SQLException en cas d'erreur de lecture du résultat
     */
    private Facture mapToEntity(ResultSet rs) throws SQLException {
        return new Facture(
                rs.getInt("id"),
                rs.getString("numero_facture"),
                rs.getInt("commande_id"),
                rs.getDouble("montant_total"),
                rs.getTimestamp("date_emission").toLocalDateTime()
        );
    }
}