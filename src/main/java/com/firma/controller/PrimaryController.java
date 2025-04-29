package com.firma.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import com.firma.model.Firma;
import com.firma.model.FirmaDAO;
import com.firma.util.HashUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class PrimaryController {
    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private TextField txtInstitucion;
    @FXML private TextField txtCargo;
    @FXML private TextField txtArchivo;
    @FXML private ComboBox<String> cmbCertificado;

    private FirmaDAO firmaDAO;
    private File archivoSeleccionado;

    @FXML
    public void initialize() {
        firmaDAO = new FirmaDAO();
        cmbCertificado.getItems().addAll("Firma Grupal", "Firma Admin");
        cmbCertificado.setValue("Firma Grupal");
    }

    @FXML
    private void seleccionarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Archivos Soportados", "*.txt", "*.p12"),
            new FileChooser.ExtensionFilter("Archivos de texto", "*.txt"),
            new FileChooser.ExtensionFilter("Archivos P12", "*.p12")
        );
        archivoSeleccionado = fileChooser.showOpenDialog(null);
        if (archivoSeleccionado != null) {
            txtArchivo.setText(archivoSeleccionado.getAbsolutePath());
            if (archivoSeleccionado.getName().toLowerCase().endsWith(".p12")) {
                try {
                    byte[] contenido = Files.readAllBytes(archivoSeleccionado.toPath());
                    String hash = HashUtils.generateFileHash(contenido);
                    Firma firmaExistente = firmaDAO.buscarPorHash(hash);
                    if (firmaExistente != null) {
                        txtCedula.setText(firmaExistente.getCedula());
                        txtNombre.setText(firmaExistente.getNombre());
                        txtInstitucion.setText(firmaExistente.getInstitucion());
                        txtCargo.setText(firmaExistente.getCargo());
                        cmbCertificado.setValue(firmaExistente.getCertificadoUsado());
                    }
                } catch (Exception e) {
                    mostrarError("Error al leer el archivo: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    private void firmarDocumento() {
        try {
            if (archivoSeleccionado == null) {
                mostrarError("Por favor seleccione un archivo");
                return;
            }
            String nombreArchivo = archivoSeleccionado.getName().toLowerCase();
            boolean esTxt = nombreArchivo.endsWith(".txt");
            boolean esP12 = nombreArchivo.endsWith(".p12");
            if (!esTxt && !esP12) {
                mostrarError("El archivo debe ser .txt o .p12");
                return;
            }

            String nombreOriginal = archivoSeleccionado.getName();
            String nombreBase = nombreOriginal.substring(0, nombreOriginal.lastIndexOf("."));
            Path rutaDestino;
            if (esTxt) {
                if (txtCedula.getText().isEmpty() || txtNombre.getText().isEmpty() ||
                    txtInstitucion.getText().isEmpty() || txtCargo.getText().isEmpty()) {
                    mostrarError("Por favor complete todos los campos");
                    return;
                }

                // Convertir de TXT a P12
                rutaDestino = Paths.get(archivoSeleccionado.getParent(), nombreBase + ".p12");
                if (Files.exists(rutaDestino)) {
                    mostrarError("Ya existe un archivo con el nombre: " + rutaDestino.getFileName());
                    return;
                }

                HashUtils.processFile(archivoSeleccionado.toPath(), rutaDestino, cmbCertificado.getValue(), true);

                Firma firma = new Firma();
                firma.setCedula(txtCedula.getText());
                firma.setNombre(txtNombre.getText());
                firma.setInstitucion(txtInstitucion.getText());
                firma.setCargo(txtCargo.getText());
                firma.setFecha(LocalDate.now());
                firma.setArchivoOriginal(archivoSeleccionado.getAbsolutePath());
                firma.setArchivoFirmado(rutaDestino.toString());
                firma.setCertificadoUsado(cmbCertificado.getValue());

                byte[] contenido = Files.readAllBytes(rutaDestino);
                firma.setArchivoHash(HashUtils.generateFileHash(contenido));
                firmaDAO.guardarFirma(firma);
                mostrarExito("Documento firmado y encriptado exitosamente como: " + rutaDestino.getFileName());

            } else {
                rutaDestino = Paths.get(archivoSeleccionado.getParent(), nombreBase + "_descifrado.txt");
                if (Files.exists(rutaDestino)) {
                    mostrarError("Ya existe un archivo con el nombre: " + rutaDestino.getFileName());
                    return;
                }

                byte[] contenido = Files.readAllBytes(archivoSeleccionado.toPath());
                String hash = HashUtils.generateFileHash(contenido);
                Firma firmaExistente = firmaDAO.buscarPorHash(hash);
                if (firmaExistente == null) {
                    mostrarError("No se encontró información del archivo en la base de datos.\n" +
                                "Este archivo P12 no fue generado por este sistema o está corrupto.");
                    return;
                }
                HashUtils.processFile(archivoSeleccionado.toPath(), rutaDestino, firmaExistente.getCertificadoUsado(), false);
                mostrarExito("Documento desencriptado exitosamente como: " + rutaDestino.getFileName());
            }
            limpiarCampos();
        } catch (Exception e) {
            mostrarError("Error al procesar el documento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void limpiarCampos() {
        txtCedula.clear();
        txtNombre.clear();
        txtInstitucion.clear();
        txtCargo.clear();
        txtArchivo.clear();
        archivoSeleccionado = null;
        cmbCertificado.setValue("Firma Grupal");
    }
}