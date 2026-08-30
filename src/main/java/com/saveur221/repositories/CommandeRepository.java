package com.saveur221.repositories;

import com.saveur221.config.DatabaseConnection;
import com.saveur221.entities.Commande;
import com.saveur221.enums.StatutCommande;
import com.saveur221.enums.StatutPaiement;

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
 * Repository dédié à l'accès aux données des commandes.
 *
 * <p>Les lignes de commande sont chargées séparément
 * par {@code LigneCommandeRepository}.</p>
 */
public class CommandeRepository implements Repository<Commande, Integer> {

    private static final String SELECT_BASE =
            "SELECT id, numero_commande, client_id, date_commande, statut, " +
            "statut_paiement, montant_total FROM commandes";

    @Override
    public Optional<Commande> findById(Integer id) {
        String sql = SELECT_BASE + " WHERE id = ?";

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
                    "Erreur lors de la recherche de la commande : " + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public List<Commande> findAll() {
        String sql = SELECT_BASE + " ORDER BY date_commande DESC";
        List<Commande> commandes = new ArrayList<>();

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                commandes.add(mapToEntity(rs));
            }

            return commandes;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la récupération des commandes : "
                            + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Recherche les commandes correspondant à un statut.
     *
     * @param statut statut recherché
     * @return liste des commandes correspondantes
     */
    public List<Commande> findByStatut(StatutCommande statut) {
        String sql = SELECT_BASE +
                " WHERE statut = ?::statut_commande ORDER BY date_commande DESC";

        List<Commande> commandes = new ArrayList<>();

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, statut.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    commandes.add(mapToEntity(rs));
                }

                return commandes;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors du filtrage des commandes : " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Récupère les commandes dont le paiement est incomplet.
     *
     * @return liste des commandes impayées ou partiellement payées
     */
    public List<Commande> findImpayeesOuPartielles() {
        String sql = SELECT_BASE +
                " WHERE statut_paiement IN ('IMPAYE', 'PARTIEL') " +
                "ORDER BY date_commande";

        List<Commande> commandes = new ArrayList<>();

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                commandes.add(mapToEntity(rs));
            }

            return commandes;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la récupération des commandes impayées : "
                            + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Recherche une commande à partir de son numéro.
     *
     * @param numeroCommande numéro de la commande
     * @return la commande correspondante si elle existe
     */
    public Optional<Commande> findByNumeroCommande(String numeroCommande) {
        String sql = SELECT_BASE + " WHERE numero_commande = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, numeroCommande);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToEntity(rs));
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la recherche de la commande par numéro : "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public Commande save(Commande entite) {
        String sql =
                "INSERT INTO commandes " +
                "(numero_commande, client_id, date_commande, statut, " +
                "statut_paiement, montant_total) " +
                "VALUES (?, ?, ?, ?::statut_commande, " +
                "?::statut_paiement_commande, ?)";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {
            stmt.setString(1, entite.getNumeroCommande());
            stmt.setInt(2, entite.getClientId());
            stmt.setTimestamp(
                    3,
                    Timestamp.valueOf(entite.getDateCommande())
            );
            stmt.setString(4, entite.getStatut().name());
            stmt.setString(5, entite.getStatutPaiement().name());
            stmt.setDouble(6, entite.getMontantTotal());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    entite.setId(keys.getInt(1));
                }
            }

            return entite;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la création de la commande : "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public void update(Commande entite) {
        String sql =
                "UPDATE commandes SET " +
                "statut = ?::statut_commande, " +
                "statut_paiement = ?::statut_paiement_commande, " +
                "montant_total = ? WHERE id = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, entite.getStatut().name());
            stmt.setString(2, entite.getStatutPaiement().name());
            stmt.setDouble(3, entite.getMontantTotal());
            stmt.setInt(4, entite.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la modification de la commande : "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM commandes WHERE id = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la suppression de la commande : "
                            + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Convertit une ligne SQL en entité Commande.
     *
     * @param rs résultat SQL contenant les données de la commande
     * @return commande correspondante
     * @throws SQLException en cas d'erreur de lecture du résultat
     */
    private Commande mapToEntity(ResultSet rs) throws SQLException {
        return new Commande(
                rs.getInt("id"),
                rs.getString("numero_commande"),
                rs.getInt("client_id"),
                rs.getTimestamp("date_commande").toLocalDateTime(),
                StatutCommande.valueOf(rs.getString("statut")),
                StatutPaiement.valueOf(rs.getString("statut_paiement")),
                rs.getDouble("montant_total")
        );
    }
}