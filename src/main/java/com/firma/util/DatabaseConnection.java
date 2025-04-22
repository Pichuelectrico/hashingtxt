package com.firma.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection {
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException, IOException {
        if (connection == null || connection.isClosed()) {
            Properties env = new Properties();
            env.load(Files.newBufferedReader(Paths.get(".env")));
            String url = env.getProperty("DB_URL");
            String user = env.getProperty("DB_USER");
            String password = env.getProperty("DB_PASSWORD");
            connection = DriverManager.getConnection(url, user, password);
            createTableIfNotExists();
        }
        return connection;
    }

    private static void createTableIfNotExists() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS firmas (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "cedula VARCHAR(20) NOT NULL, " +
            "nombre VARCHAR(100) NOT NULL, " +
            "institucion VARCHAR(100) NOT NULL, " +
            "cargo VARCHAR(100) NOT NULL, " +
            "fecha DATE NOT NULL, " +
            "archivo_original VARCHAR(255) NOT NULL, " +
            "archivo_firmado VARCHAR(255) NOT NULL, " +
            "certificado_usado VARCHAR(50) NOT NULL, " +
            "archivo_hash VARCHAR(64), " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }
}