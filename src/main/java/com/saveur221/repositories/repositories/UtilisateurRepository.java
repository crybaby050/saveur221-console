package com.saveur221.repositories;

import com.saveur221.config.DatabaseConnection;
import com.saveur221.entities.Utilisateur;
import com.saveur221.enums.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtilisateurRepository implements Repository<Utilisateur, Integer> {

    // Le rôle est stocké en base via une clé étrangère vers la table
    // "roles" (et non une simple colonne texte) — chaque requête de lecture
    // fait donc une jointure pour reconstruire l'enum Role côté Java.
    private static final String SELECT_BASE = "SELECT u.id, u.nom, u.prenom, u.email, u.mot_de_passe, u.actif, r.nom AS role_nom "
            +
            "FROM utilisateurs u JOIN roles r ON u.role_id = r.id";

    @Override
    public Optional<Utilisateur> findById(Integer id) {
        String sql = SELECT_BASE + " WHERE u.id = ?";

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
            throw new RuntimeException("Erreur lors de la recherche de l'utilisateur : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Utilisateur> findAll() {
        String sql = SELECT_BASE + " ORDER BY u.nom, u.prenom";
        List<Utilisateur> utilisateurs = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                utilisateurs.add(mapToEntity(rs));
            }
            return utilisateurs;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des utilisateurs : " + e.getMessage(), e);
        }
    }

    // Utilisée par AuthService (US "S'authentifier") et par
    // UtilisateurService pour vérifier l'unicité de l'email à la création.
    public Optional<Utilisateur> findByEmail(String email) {
        String sql = SELECT_BASE + " WHERE u.email = ?";

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
            throw new RuntimeException("Erreur lors de la recherche de l'utilisateur par email : " + e.getMessage(), e);
        }
    }

    // US "Rechercher un utilisateur interne".
    public List<Utilisateur> rechercherParNom(String motCle) {
        String sql = SELECT_BASE + " WHERE u.nom ILIKE ? OR u.prenom ILIKE ? ORDER BY u.nom, u.prenom";
        List<Utilisateur> utilisateurs = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            String motion = "%" + motCle + "%";
            stmt.setString(1, motion);
            stmt.setString(2, motion);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    utilisateurs.add(mapToEntity(rs));
                }
                return utilisateurs;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche d'utilisateurs : " + e.getMessage(), e);
        }
    }

    @Override
    public Utilisateur save(Utilisateur entite) {
        String sql = "INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, actif, role_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, entite.getNom());
            stmt.setString(2, entite.getPrenom());
            stmt.setString(3, entite.getEmail());
            stmt.setString(4, entite.getMotDePasse());
            stmt.setBoolean(5, entite.isActif());
            stmt.setInt(6, resoudreRoleId(conn, entite.getRole()));
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    entite.setId(keys.getInt(1));
                }
            }
            return entite;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de l'utilisateur : " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Utilisateur entite) {
        String sql = "UPDATE utilisateurs SET nom = ?, prenom = ?, email = ?, actif = ?, role_id = ? " +
                "WHERE id = ?";
        // Remarque : le mot de passe n'est volontairement pas mis à jour ici.
        // Un changement de mot de passe doit passer par une méthode dédiée
        // (UtilisateurService.changerMotDePasse), pas par cette méthode
        // générique, pour éviter d'écraser un hash par erreur.

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entite.getNom());
            stmt.setString(2, entite.getPrenom());
            stmt.setString(3, entite.getEmail());
            stmt.setBoolean(4, entite.isActif());
            stmt.setInt(5, resoudreRoleId(conn, entite.getRole()));
            stmt.setInt(6, entite.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification de l'utilisateur : " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM utilisateurs WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'utilisateur : " + e.getMessage(), e);
        }
    }

    // Traduit l'enum Role côté Java vers l'id correspondant dans la table
    // "roles" — nécessaire car la colonne utilisateurs.role_id est une
    // clé étrangère, pas le nom du rôle directement.
    private int resoudreRoleId(Connection conn, Role role) throws SQLException {
        String sql = "SELECT id FROM roles WHERE nom = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role.name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
                throw new SQLException("Rôle inconnu en base : " + role.name());
            }
        }
    }

    private Utilisateur mapToEntity(ResultSet rs) throws SQLException {
        return new Utilisateur(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("prenom"),
                rs.getString("email"),
                rs.getString("mot_de_passe"),
                rs.getBoolean("actif"),
                Role.valueOf(rs.getString("role_nom")));
    }
}