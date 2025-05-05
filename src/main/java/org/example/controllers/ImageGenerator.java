package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import org.example.services.Flux_service;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class ImageGenerator {
    @FXML private TextArea descriptionTextArea;
    @FXML private ComboBox<Integer> sizeComboBox;
    @FXML private ComboBox<String> styleComboBox;
    @FXML private ImageView resultImageView;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Button generateButton;
    @FXML private Button saveButton;

    private final Flux_service fluxService = new Flux_service();
    private byte[] currentImageData;

    @FXML
    public void initialize() {
        // Initialisation des ComboBox
        sizeComboBox.getItems().addAll(256, 512, 1024);
        sizeComboBox.setValue(512);

        styleComboBox.getItems().addAll(
                "Digital Art",
                "WILDLIFE",
                "LANDSCAPE",
                "3D Render",
                "Urban Scene",
                "PORTRAIT"
        );
        styleComboBox.setValue("Digital Art");

        saveButton.setDisable(true);
    }

    @FXML
    private void handleGenerateImage() {
        String description = descriptionTextArea.getText();
        Integer size = sizeComboBox.getValue();
        String style = styleComboBox.getValue();

        if (!fluxService.validateParameters(description, size)) {
            showAlert("Erreur", "Veuillez entrer une description valide et sélectionner une taille");
            return;
        }

        toggleUIState(true);

        new Thread(() -> {
            try {
                currentImageData = fluxService.generateImage(description, size, style);

                javafx.application.Platform.runLater(() -> {
                    Image image = fluxService.convertToJavaFXImage(currentImageData);
                    resultImageView.setImage(image);
                    saveButton.setDisable(false);
                    toggleUIState(false);
                });
            } catch (IOException e) {
                javafx.application.Platform.runLater(() -> {
                    showAlert("Erreur de génération", e.getMessage());
                    toggleUIState(false);
                });
            }
        }).start();
    }

    @FXML
    private void handleSaveImage() {
        if (currentImageData == null) return;

        Path outputPath = Paths.get(System.getProperty("user.home"), "Downloads", "generated_image.png");

        try {
            fluxService.saveImage(currentImageData, outputPath);
            showAlert("Succès", "Image sauvegardée dans :\n" + outputPath);
        } catch (IOException e) {
            showAlert("Erreur", "Échec de sauvegarde : " + e.getMessage());
        }
    }

    private void toggleUIState(boolean processing) {
        progressIndicator.setVisible(processing);
        generateButton.setDisable(processing);
        descriptionTextArea.setDisable(processing);
        sizeComboBox.setDisable(processing);
        styleComboBox.setDisable(processing);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}