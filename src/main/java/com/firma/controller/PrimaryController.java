package com.firma.controller;

import java.io.File;
import java.io.IOException;

import com.firma.model.P12Entry;
import com.firma.util.p12Util;
import com.firma.util.Firmador;
import com.firma.util.DatabaseConnection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.sql.SQLException;

public class PrimaryController {

    @FXML
    private Button file;

    @FXML
    private Button file2;

    @FXML
    private TextField fileDrag;

    @FXML
    private TextField fileDrop;

    @FXML
    private TextField fileDrop2;

    @FXML
    private PasswordField pass2;
    
    @FXML
    private Label data2;

    @FXML
    private Button firma;

    @FXML
    private TextField firmaDrop2;

    @FXML
    private TextField firmaDrop21;

    @FXML
    private PasswordField gen;

    @FXML
    private Button firmarDoc;

    @FXML
    private Button frima2;
    
    @FXML
    private TextField firmaDrop211;

    @FXML
    private PasswordField psswrd;

    @FXML
    private Button verDoc;

    @FXML
    private Button verSign;

    @FXML
    private TextArea data1;

    private Stage stage;
    private File doc;
    private File sign;
    private File selectedDirectory;
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    void dragDrop(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;

        if (db.hasFiles()) {
            File file = db.getFiles().get(0);
            String fileName = file.getName();
            String nodeId = ((javafx.scene.Node) event.getSource()).getId();

            switch (nodeId) {
                case "fileDrop":
                case "fileDrop2":
                    if (!fileName.endsWith(".pdf")) {
                        mostrarAlerta("Tipo de archivo inválido", "Tipo de archivo inválido", "Subir solo archivos PDF");
                        break;
                    }
                    doc = file;
                    if (nodeId.equals("fileDrop")) {
                        fileDrop.setText("Archivo: " + fileName);
                    } else {
                        fileDrop2.setText("Archivo: " + fileName);
                    }
                    success = true;
                    break;

                case "fileDrag":
                case "firmaDrop2":
                    if (!fileName.endsWith(".p12")) {
                        mostrarAlerta("Tipo de archivo inválido", "Tipo de archivo inválido", "Subir solo archivos P12");
                        break;
                    }
                    sign = file;
                    if (nodeId.equals("fileDrag")) {
                        fileDrag.setText("Archivo: " + fileName);
                    } else {
                        firmaDrop2.setText("Archivo: " + fileName);
                    }
                    success = true;
                    break;

                default:
                    mostrarAlerta("Error", "Nodo desconocido", "El nodo de destino no es válido");
            }
        }

        event.setDropCompleted(success);
        event.consume();
    }

    @FXML
    void dragOver(DragEvent event) {
        if (event.getGestureSource() != event.getTarget()
         &&
            event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    @FXML
    void firmar(ActionEvent event) {
        System.out.println(doc);
        System.out.println(sign);
        System.out.println(psswrd.getText());

        P12Entry firma;
        try {
            
            // Aquí puedes usar la conexión para realizar operaciones de base de datos
            // Por ejemplo, guardar información sobre el documento firmado

            firma = p12Util.mostrarContenidoP12(sign.getAbsolutePath(), psswrd.getText()).get(0);
            Firmador.firmar(doc.getPath(), firma.getAlias(), firma.getKey(), firma.getCertificate());
            mostrarAlertaExito("Firmado", "Firmado", "Pdf firmado correctamente");
            DatabaseConnection.insertarFirma(firma.getAlias(), doc.getName(), "yes", firma.getCertificate().toString(), firma.getAlias(), firma.getCertificate().getPublicKey().toString());            
            // Cerrar la conexión después de usarla
            
        } catch (SQLException e) {
            mostrarAlerta("Error de Base de Datos", "No se pudo conectar a la base de datos", e.getMessage());
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    @FXML
    void verificarPdf(ActionEvent event) {
        String signatureInfo ;
        try {
            signatureInfo = Firmador.obtenerFirma(doc.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        data1.setText(signatureInfo);
    }

    @FXML
    void verificarFirma(ActionEvent event) {
        P12Entry firma;
        try {
            firma = p12Util.mostrarContenidoP12(sign.getAbsolutePath(), pass2.getText()).get(0);
            String signatureInfo = firma.getCertificate().getSubjectX500Principal().toString();
            
            // Aquí se realiza la consulta a la base de datos para verificar si el certificado ya ha sido utilizado
            boolean certificadoUsado = DatabaseConnection.verificarCertificadoUsado(signatureInfo);
            
            if (certificadoUsado) {
                data2.setText("Certificado valido");
            } else {
                data2.setText(signatureInfo);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al verificar firma", "No se pudo verificar la firma: " + e.getMessage());
            return;
        }
    }

    @FXML
    void generateP12(ActionEvent event) {
        String nombre = "usuario1";
        String password = "123456";
        String archivoSalida = selectedDirectory.getAbsolutePath() + nombre + ".p12";
        int numTries;
        try {
            numTries = DatabaseConnection.contarElementos()+1;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            p12Util.crearArchivoP12(nombre, password, archivoSalida, numTries);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openFirma(ActionEvent event) {

    }

    @FXML
    void reestablecer(ActionEvent event) {

    }

    @FXML
    void seleccionarArchivo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar certificado");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            Button boton = (Button) event.getSource();
            String buttonId = boton.getId();
            switch (buttonId) {
                case "file":
                    fileDrop.setText(file.getName());
                    break;
                case "file2":
                    fileDrop2.setText(file.getName());
                    break;
                case "firma":
                    fileDrag.setText(file.getName());
                    break;
                case "firma2":
                    firmaDrop2.setText(file.getName());
                    break;
                default:
                    throw new AssertionError();
            }
        }
    }

    @FXML
    void seleccionarDirectorio(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar carpeta");
        selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            Button boton = (Button) event.getSource();
            String buttonId = boton.getId();
            switch (buttonId) {
                case "file":
                    fileDrop.setText(selectedDirectory.getName());
                    break;
                case "file2":
                    fileDrop2.setText(selectedDirectory.getName());
                    break;
                case "firma":
                    fileDrag.setText(selectedDirectory.getName());
                    break;
                case "firma2":
                    firmaDrop2.setText(selectedDirectory.getName());
                    break;
                case "firma3":
                    firmaDrop211.setText(selectedDirectory.getName());
                    break;
                default:
                    throw new AssertionError();
            }
        }
    }

    private void mostrarAlerta(String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    private void mostrarAlertaExito(String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

}
