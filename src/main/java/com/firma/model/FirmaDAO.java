package com.firma.model;

import com.firma.util.DatabaseConnection;

import java.io.IOException;
import java.sql.*;

public class FirmaDAO {
    public void guardarFirma(Firma firma) throws SQLException, IOException {
        String sql = "INSERT INTO firmas (cedula, nombre, institucion, cargo, fecha, " +
                    "archivo_original, archivo_firmado, certificado_usado, archivo_hash) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, firma.getCedula());
            pstmt.setString(2, firma.getNombre());
            pstmt.setString(3, firma.getInstitucion());
            pstmt.setString(4, firma.getCargo());
            pstmt.setDate(5, Date.valueOf(firma.getFecha()));
            pstmt.setString(6, firma.getArchivoOriginal());
            pstmt.setString(7, firma.getArchivoFirmado());
            pstmt.setString(8, firma.getCertificadoUsado());
            pstmt.setString(9, firma.getArchivoHash());
            pstmt.executeUpdate();
        }
    }

    public Firma buscarPorHash(String hash) throws SQLException, IOException {
        String sql = "SELECT * FROM firmas WHERE archivo_hash = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hash);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Firma firma = new Firma();
                    firma.setCedula(rs.getString("cedula"));
                    firma.setNombre(rs.getString("nombre"));
                    firma.setInstitucion(rs.getString("institucion"));
                    firma.setCargo(rs.getString("cargo"));
                    firma.setFecha(rs.getDate("fecha").toLocalDate());
                    firma.setArchivoOriginal(rs.getString("archivo_original"));
                    firma.setArchivoFirmado(rs.getString("archivo_firmado"));
                    firma.setCertificadoUsado(rs.getString("certificado_usado"));
                    firma.setArchivoHash(rs.getString("archivo_hash"));
                    return firma;
                }
            }
        }
        return null;
    }
}