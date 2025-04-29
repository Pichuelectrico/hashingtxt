package com.firma.controller;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    private Button firma;

    @FXML
    private TextField firmaDrop2;

    @FXML
    private Button firmarDoc;

    @FXML
    private Button frima2;

    @FXML
    private PasswordField psswrd;

    @FXML
    private Button verDoc;

    @FXML
    private Button verSign;

    private Stage stage;
    private File doc;
    private File sign;
    
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

    private void mostrarAlerta(String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

}
