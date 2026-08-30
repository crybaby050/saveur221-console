package com.saveur221.repositories;

import com.saveur221.config.DatabaseConnection;
import com.saveur221.entities.Paiement;

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
 * Repository dédié à l'accès aux données des paiements.
 */
public class PaiementRepository implements Repository<Paiement, Integer> {

    @Override
    public Optional<Paiement> findById(Integer id) {
        String sql = "SELECT id, commande_id, montant, date_paiement " +
                     "FROM paiements WHERE id = ?";

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
                    "Erreur lors de la recherche du paiement : "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public List<Paiement> findAll() {
        String sql =
                "SELECT id, commande_id, montant, date_paiement " +
                "FROM paiements ORDER BY date_paiement DESC";

        List<Paiement> paiements = new ArrayList<>();

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                paiements.add(mapToEntity(rs));
            }

            return paiements;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la récupération des paiements : "
                            + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Récupère les paiements associés à une commande.
     *
     * @param commandeId identifiant de la commande
     * @return liste des paiements de la commande
     */
    public List<Paiement> findByCommandeId(int commandeId) {
        String sql =
                "SELECT id, commande_id, montant, date_paiement " +
                "FROM paiements WHERE commande_id = ? " +
                "ORDER BY date_paiement";

        List<Paiement> paiements = new ArrayList<>();

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, commandeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    paiements.add(mapToEntity(rs));
                }

                return paiements;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la récupération des paiements de la commande : "
                            + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Calcule le montant total déjà payé pour une commande.
     *
     * @param commandeId identifiant de la commande
     * @return somme des paiements enregistrés
     */
    public double sommePaiements(int commandeId) {
        String sql =
                "SELECT COALESCE(SUM(montant), 0) AS total " +
                "FROM paiements WHERE commande_id = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, commandeId);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getDouble("total");
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors du calcul de la somme des paiements : "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public Paiement save(Paiement entite) {
        String sql =
                "INSERT INTO paiements " +
                "(commande_id, montant, date_paiement) " +
                "VALUES (?, ?, ?)";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {
            stmt.setInt(1, entite.getCommandeId());
            stmt.setDouble(2, entite.getMontant());
            stmt.setTimestamp(
                    3,
                    Timestamp.valueOf(entite.getDatePaiement())
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
                    "Erreur lors de l'enregistrement du paiement : "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public void update(Paiement entite) {
        String sql =
                "UPDATE paiements SET montant = ? WHERE id = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setDouble(1, entite.getMontant());
            stmt.setInt(2, entite.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la modification du paiement : "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM paiements WHERE id = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la suppression du paiement : "
                            + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Convertit une ligne SQL en entité Paiement.
     *
     * @param rs résultat SQL contenant les données du paiement
     * @return paiement correspondant
     * @throws SQLException en cas d'erreur de lecture du résultat
     */
    private Paiement mapToEntity(ResultSet rs) throws SQLException {
        return new Paiement(
                rs.getInt("id"),
                rs.getInt("commande_id"),
                rs.getDouble("montant"),
                rs.getTimestamp("date_paiement").toLocalDateTime()
        );
    }
}