package com.saveur221.repositories;

import com.saveur221.config.DatabaseConnection;
import com.saveur221.entities.Recu;
import com.saveur221.enums.TypePaiementRecu;

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
 * Repository JDBC dédié à la gestion des reçus.
 *
 * <p>
 * Un reçu est associé à un paiement unique. Une commande peut donc
 * posséder plusieurs reçus lorsqu'elle est réglée en plusieurs paiements.
 * </p>
 */
public class RecuRepository implements Repository<Recu, Integer> {

    /**
     * Recherche un reçu à partir de son identifiant.
     *
     * @param id identifiant du reçu
     * @return le reçu correspondant s'il existe
     */
    @Override
    public Optional<Recu> findById(Integer id) {

        String sql = "SELECT id, numero_recu, paiement_id, type_paiement, montant, date_emission " +
                "FROM recus WHERE id = ?";

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
            throw new RuntimeException(
                    "Erreur lors de la recherche du reçu : " + e.getMessage(), e);
        }
    }

    /**
     * Récupère l'ensemble des reçus, du plus récent au plus ancien.
     *
     * @return liste des reçus
     */
    @Override
    public List<Recu> findAll() {

        String sql = "SELECT id, numero_recu, paiement_id, type_paiement, montant, date_emission " +
                "FROM recus ORDER BY date_emission DESC";

        List<Recu> recus = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                recus.add(mapToEntity(rs));
            }

            return recus;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la récupération des reçus : " + e.getMessage(), e);
        }
    }

    /**
     * Recherche le reçu associé à un paiement.
     *
     * <p>
     * La relation paiement-reçu étant de type 1-1, un paiement ne peut
     * posséder qu'un seul reçu.
     * </p>
     *
     * @param paiementId identifiant du paiement
     * @return le reçu associé s'il existe
     */
    public Optional<Recu> findByPaiementId(int paiementId) {

        String sql = "SELECT id, numero_recu, paiement_id, type_paiement, montant, date_emission " +
                "FROM recus WHERE paiement_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, paiementId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(mapToEntity(rs));
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la recherche du reçu du paiement : " + e.getMessage(), e);
        }
    }

    /**
     * Récupère les reçus associés à une commande.
     *
     * <p>
     * La table {@code recus} ne contient pas directement l'identifiant
     * de la commande. La relation est donc obtenue par une jointure
     * avec la table {@code paiements}.
     * </p>
     *
     * @param commandeId identifiant de la commande
     * @return liste des reçus de la commande
     */
    public List<Recu> findByCommandeId(int commandeId) {

        String sql = "SELECT r.id, r.numero_recu, r.paiement_id, r.type_paiement, r.montant, r.date_emission " +
                "FROM recus r JOIN paiements p ON r.paiement_id = p.id " +
                "WHERE p.commande_id = ? ORDER BY r.date_emission";

        List<Recu> recus = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, commandeId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    recus.add(mapToEntity(rs));
                }

                return recus;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la récupération des reçus de la commande : "
                            + e.getMessage(),
                    e);
        }
    }

    /**
     * Enregistre un nouveau reçu et récupère son identifiant généré.
     *
     * @param entite reçu à enregistrer
     * @return le reçu enregistré
     */
    @Override
    public Recu save(Recu entite) {

        String sql = "INSERT INTO recus (numero_recu, paiement_id, type_paiement, montant, date_emission) " +
                "VALUES (?, ?, ?::type_paiement_recu, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, entite.getNumeroRecu());
            stmt.setInt(2, entite.getPaiementId());
            stmt.setString(3, entite.getTypePaiement().name());
            stmt.setDouble(4, entite.getMontant());
            stmt.setTimestamp(5, Timestamp.valueOf(entite.getDateEmission()));

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {

                if (keys.next()) {
                    entite.setId(keys.getInt(1));
                }
            }

            return entite;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la création du reçu : " + e.getMessage(), e);
        }
    }

    /**
     * Met à jour le type de paiement associé au reçu.
     *
     * <p>
     * Cette méthode est principalement présente pour respecter le contrat
     * {@code Repository}. Les reçus étant des documents comptables émis,
     * leur modification doit rester exceptionnelle.
     * </p>
     *
     * @param entite reçu à modifier
     */
    @Override
    public void update(Recu entite) {

        String sql = "UPDATE recus SET type_paiement = ?::type_paiement_recu WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entite.getTypePaiement().name());
            stmt.setInt(2, entite.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la modification du reçu : " + e.getMessage(), e);
        }
    }

    /**
     * Supprime un reçu à partir de son identifiant.
     *
     * @param id identifiant du reçu
     */
    @Override
    public void deleteById(Integer id) {

        String sql = "DELETE FROM recus WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la suppression du reçu : " + e.getMessage(), e);
        }
    }

    /**
     * Convertit une ligne SQL en entité {@link Recu}.
     *
     * @param rs résultat SQL courant
     * @return entité reconstruite à partir des données de la base
     * @throws SQLException en cas d'erreur de lecture du résultat
     */
    private Recu mapToEntity(ResultSet rs) throws SQLException {

        return new Recu(
                rs.getInt("id"),
                rs.getString("numero_recu"),
                rs.getInt("paiement_id"),
                TypePaiementRecu.valueOf(rs.getString("type_paiement")),
                rs.getDouble("montant"),
                rs.getTimestamp("date_emission").toLocalDateTime());
    }
}

