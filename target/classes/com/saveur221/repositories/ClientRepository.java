package com.saveur221.repositories;

import com.saveur221.config.DatabaseConnection;
import com.saveur221.entities.Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientRepository implements LectureSeuleRepository<Client, Integer> {

    @Override
    public Optional<Client> findById(Integer id) {
        String sql = "SELECT id, nom, prenom, email, mot_de_passe, telephone, adresse " +
                "FROM clients WHERE id = ?";

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
            throw new RuntimeException("Erreur lors de la recherche du client : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Client> findAll() {
        String sql = "SELECT id, nom, prenom, email, mot_de_passe, telephone, adresse " +
                "FROM clients ORDER BY nom, prenom";
        List<Client> clients = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clients.add(mapToEntity(rs));
            }
            return clients;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des clients : " + e.getMessage(), e);
        }
    }

    // Utilisée pour rattacher une vente au comptoir à un client existant
    // (US "Enregistrer une commande sur place").
    public Optional<Client> findByEmail(String email) {
        String sql = "SELECT id, nom, prenom, email, mot_de_passe, telephone, adresse " +
                "FROM clients WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToEntity(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du client par email : " + e.getMessage(), e);
        }
    }

    // Même besoin, mais recherche par téléphone — souvent plus rapide à
    // communiquer oralement au comptoir qu'un email.
    public List<Client> rechercherParTelephone(String motCle) {
        String sql = "SELECT id, nom, prenom, email, mot_de_passe, telephone, adresse " +
                "FROM clients WHERE telephone ILIKE ? ORDER BY nom, prenom";
        List<Client> clients = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + motCle + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clients.add(mapToEntity(rs));
                }
                return clients;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du client par téléphone : " + e.getMessage(), e);
        }
    }

    // Pas de save/update/deleteById : voir LectureSeuleRepository — la
    // gestion des clients reste entièrement portée par le module PHP Web.

    private Client mapToEntity(ResultSet rs) throws SQLException {
        return new Client(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("email"),
                rs.getString("mot_de_passe"),
                rs.getString("telephone"),
                rs.getString("adresse"));
    }
}