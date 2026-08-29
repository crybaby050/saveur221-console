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

public class RecuRepository implements Repository<Recu, Integer> {

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
            throw new RuntimeException("Erreur lors de la recherche du reçu : " + e.getMessage(), e);
        }
    }

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
            throw new RuntimeException("Erreur lors de la récupération des reçus : " + e.getMessage(), e);
        }
    }

    // Relation 1-1 avec Paiement : un reçu par paiement, sans exception.
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
            throw new RuntimeException("Erreur lors de la recherche du reçu du paiement : " + e.getMessage(), e);
        }
    }

    // Une commande peut avoir plusieurs paiements, donc plusieurs reçus —
    // jointure nécessaire puisque recus ne connaît pas directement commande_id.
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
            throw new RuntimeException("Erreur lors de la récupération des reçus de la commande : " + e.getMessage(),
                    e);
        }
    }

    @Override
    public Recu save(Recu entite) {
        String sql = "INSERT INTO recus (numero_recu, paiement_id, type_paiement, montant, date_emission) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
            throw new RuntimeException("Erreur lors de la création du reçu : " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Recu entite) {
        // Un reçu émis n'a pas vocation à être modifié — fournie pour
        // respecter le contrat Repository<T, ID>, non utilisée en pratique.
        String sql = "UPDATE recus SET type_paiement = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entite.getTypePaiement().name());
            stmt.setInt(2, entite.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification du reçu : " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM recus WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du reçu : " + e.getMessage(), e);
        }
    }

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