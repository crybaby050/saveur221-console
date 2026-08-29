package com.saveur221.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Point d'accès unique à la connexion JDBC PostgreSQL.
 *
 * <p>
 * Les identifiants de connexion sont lus depuis un fichier {@code .env}
 * placé à la racine du projet (jamais committé dans Git — voir
 * {@code .gitignore}). Un fichier {@code .env.example} documente les
 * variables attendues pour toute personne clonant le dépôt.
 * </p>
 *
 * <p>
 * Implémentation volontairement minimale à ce stade du projet : une seule
 * connexion réutilisée tant qu'elle reste ouverte. Elle sera remplacée par un
 * pool de connexions (HikariCP) une fois l'injection de dépendances mise en
 * place.
 * </p>
 */
public class DatabaseConnection {

    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing() // évite un crash si le fichier .env n'existe pas encore (ex. sur un
                               // environnement de CI)
            .load();

    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    private static Connection connection;

    /**
     * Constructeur privé : cette classe n'a pas vocation à être instanciée,
     * seule sa méthode statique {@link #getConnection()} est utilisée.
     */
    private DatabaseConnection() {
    }

    /**
     * Retourne la connexion JDBC active, en la (re)créant si nécessaire.
     *
     * @return une connexion PostgreSQL ouverte et prête à l'emploi
     * @throws RuntimeException si les variables d'environnement sont
     *                          manquantes, ou si la connexion à la base de données
     *                          échoue
     */
    public static Connection getConnection() {
        if (URL == null || USER == null) {
            throw new RuntimeException(
                    "Variables d'environnement DB_URL / DB_USER manquantes. " +
                            "Vérifie la présence et le contenu du fichier .env à la racine du projet.");
        }

        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Impossible de se connecter à la base de données PostgreSQL : " + e.getMessage(), e);
        }
    }
}