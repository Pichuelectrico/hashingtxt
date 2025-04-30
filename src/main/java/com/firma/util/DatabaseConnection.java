package com.firma.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection {
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException, IOException {
        if (connection == null || connection.isClosed()) {
            String url = "jdbc:mysql://localhost:3306/mi_base";
            String user = "root";
            String password = "awita2";
            connection = DriverManager.getConnection(url, user, password);
            createTableIfNotExists();
        }
        return connection;
    }

    private static void createTableIfNotExists() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS firmas (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "nombre VARCHAR(100) NOT NULL, " +
            "archivo_original VARCHAR(255) NOT NULL, " +
            "archivo_firmado VARCHAR(255) NOT NULL, " +
            "certificado_usado VARCHAR(50) NOT NULL, " +
            "alias_certificado VARCHAR(100), " +
            "hash_clave_privada VARCHAR(256), " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    public static void insertarFirma(String nombre, String archivoOriginal, String archivoFirmado, String certificadoUsado, String aliasCertificado, String hashClavePrivada) throws SQLException, IOException {
        String insertSQL = "INSERT INTO firmas (nombre, archivo_original, archivo_firmado, certificado_usado, alias_certificado, hash_clave_privada) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, archivoOriginal);
            pstmt.setString(3, archivoFirmado);
            pstmt.setString(4, certificadoUsado);
            pstmt.setString(5, aliasCertificado);
            pstmt.setString(6, hashClavePrivada);
            pstmt.executeUpdate();
        }
    }

    public static boolean verificarDocumento(String archivoFirmado) throws SQLException, IOException {
        String querySQL = "SELECT COUNT(*) FROM firmas WHERE archivo_firmado = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(querySQL)) {
            pstmt.setString(1, archivoFirmado);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public static boolean verificarCertificadoUsado(String certificadoUsado) throws SQLException, IOException {
        String querySQL = "SELECT COUNT(*) FROM firmas WHERE certificado_usado = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(querySQL)) {
            pstmt.setString(1, certificadoUsado);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public static int contarElementos() throws SQLException, IOException {
        String countSQL = "SELECT COUNT(*) FROM firmas";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(countSQL)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}