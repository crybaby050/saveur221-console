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

/**
 * Repository JDBC dédié à la gestion des utilisateurs internes.
 *
 * <p>
 * Le rôle d'un utilisateur est référencé par une clé étrangère vers
 * la table {@code roles}. Les opérations de lecture utilisent donc
 * une jointure afin de reconstruire l'enum {@link Role}.
 * </p>
 */
public class UtilisateurRepository implements Repository<Utilisateur, Integer> {

    /**
     * Requête de base utilisée pour récupérer un utilisateur avec son rôle.
     */
    private static final String SELECT_BASE =
            "SELECT u.id, u.nom, u.prenom, u.email, u.mot_de_passe, u.actif, r.nom AS role_nom "
                    + "FROM utilisateurs u JOIN roles r ON u.role_id = r.id";

    /**
     * Recherche un utilisateur à partir de son identifiant.
     *
     * @param id identifiant de l'utilisateur
     * @return l'utilisateur correspondant s'il existe
     */
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
            throw new RuntimeException(
                    "Erreur lors de la recherche de l'utilisateur : " + e.getMessage(), e);
        }
    }

    /**
     * Récupère tous les utilisateurs, triés par nom puis par prénom.
     *
     * @return liste des utilisateurs
     */
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
            throw new RuntimeException(
                    "Erreur lors de la récupération des utilisateurs : " + e.getMessage(), e);
        }
    }

    /**
     * Recherche un utilisateur à partir de son adresse email.
     *
     * <p>
     * Cette méthode est notamment utilisée lors de l'authentification
     * et pour vérifier l'unicité de l'email avant création.
     * </p>
     *
     * @param email adresse email recherchée
     * @return l'utilisateur correspondant s'il existe
     */
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
            throw new RuntimeException(
                    "Erreur lors de la recherche de l'utilisateur par email : "
                            + e.getMessage(),
                    e);
        }
    }

    /**
     * Recherche des utilisateurs par nom ou prénom.
     *
     * <p>
     * La recherche est insensible à la casse grâce à l'opérateur
     * PostgreSQL {@code ILIKE}.
     * </p>
     *
     * @param motCle terme recherché
     * @return liste des utilisateurs correspondants
     */
    public List<Utilisateur> rechercherParNom(String motCle) {

        String sql = SELECT_BASE
                + " WHERE u.nom ILIKE ? OR u.prenom ILIKE ? ORDER BY u.nom, u.prenom";

        List<Utilisateur> utilisateurs = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            String pattern = "%" + motCle + "%";

            stmt.setString(1, pattern);
            stmt.setString(2, pattern);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    utilisateurs.add(mapToEntity(rs));
                }

                return utilisateurs;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la recherche d'utilisateurs : " + e.getMessage(), e);
        }
    }

    /**
     * Enregistre un nouvel utilisateur et récupère son identifiant généré.
     *
     * <p>
     * Le rôle est converti en identifiant de la table {@code roles}
     * avant l'insertion.
     * </p>
     *
     * @param entite utilisateur à enregistrer
     * @return utilisateur persisté
     */
    @Override
    public Utilisateur save(Utilisateur entite) {

        String sql = "INSERT INTO utilisateurs "
                + "(nom, prenom, email, mot_de_passe, actif, role_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS)) {

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
            throw new RuntimeException(
                    "Erreur lors de la création de l'utilisateur : " + e.getMessage(), e);
        }
    }

    /**
     * Met à jour les informations générales d'un utilisateur.
     *
     * <p>
     * Le mot de passe n'est volontairement pas modifié par cette méthode.
     * Son changement doit passer par une opération dédiée du service
     * afin de garantir le traitement correct du mot de passe.
     * </p>
     *
     * @param entite utilisateur contenant les nouvelles informations
     */
    @Override
    public void update(Utilisateur entite) {

        String sql = "UPDATE utilisateurs SET nom = ?, prenom = ?, email = ?, "
                + "actif = ?, role_id = ? WHERE id = ?";

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
            throw new RuntimeException(
                    "Erreur lors de la modification de l'utilisateur : "
                            + e.getMessage(),
                    e);
        }
    }

    /**
     * Supprime un utilisateur à partir de son identifiant.
     *
     * @param id identifiant de l'utilisateur à supprimer
     */
    @Override
    public void deleteById(Integer id) {

        String sql = "DELETE FROM utilisateurs WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erreur lors de la suppression de l'utilisateur : "
                            + e.getMessage(),
                    e);
        }
    }

    /**
     * Résout l'identifiant du rôle correspondant à un enum {@link Role}.
     *
     * @param conn connexion JDBC active
     * @param role rôle à rechercher
     * @return identifiant du rôle en base
     * @throws SQLException si le rôle n'existe pas ou en cas d'erreur SQL
     */
    private int resoudreRoleId(Connection conn, Role role) throws SQLException {

        String sql = "SELECT id FROM roles WHERE nom = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role.name());

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("id");
                }

                throw new SQLException(
                        "Rôle inconnu en base : " + role.name());
            }
        }
    }

    /**
     * Convertit une ligne SQL en entité {@link Utilisateur}.
     *
     * @param rs résultat SQL courant
     * @return utilisateur reconstruit à partir des données de la base
     * @throws SQLException en cas d'erreur de lecture du résultat
     */
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