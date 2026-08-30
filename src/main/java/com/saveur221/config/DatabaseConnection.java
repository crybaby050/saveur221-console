package com.saveur221.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gère la connexion JDBC à la base de données PostgreSQL.
 *
 * <p>Les paramètres de connexion sont chargés depuis le fichier {@code .env}.</p>
 */
public class DatabaseConnection {

    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    private static Connection connection;

    /**
     * Empêche l'instanciation de cette classe utilitaire.
     */
    private DatabaseConnection() {
    }

    /**
     * Retourne la connexion active ou en crée une nouvelle si nécessaire.
     *
     * @return une connexion PostgreSQL ouverte
     * @throws RuntimeException si la configuration ou la connexion échoue
     */
    public static Connection getConnection() {
        if (URL == null || USER == null) {
            throw new RuntimeException(
                    "Variables d'environnement DB_URL / DB_USER manquantes. " +
                    "Vérifie la présence et le contenu du fichier .env."
            );
        }

        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }

            return connection;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Impossible de se connecter à la base de données PostgreSQL : "
                            + e.getMessage(),
                    e
            );
        }
    }
}