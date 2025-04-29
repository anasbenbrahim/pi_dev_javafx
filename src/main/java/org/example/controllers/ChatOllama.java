package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.services.Ollama;

import java.io.IOException;

public class ChatOllama {

    private Parent root;
    private Scene scene;
    private Stage stage;

    @FXML
    private TextField messageField;

    @FXML
    private TextArea responseArea;

    @FXML
    private Button sendButton;

    public void sendMessage(ActionEvent event) {
        // Désactiver le bouton pendant le traitement
        sendButton.setDisable(true);
        responseArea.appendText("Vous: " + messageField.getText() + "\n\n");
        responseArea.appendText("Système: En cours de traitement...\n\n");

        // Créer une tâche pour l'appel API
        new Thread(() -> {
            try {
                String message = messageField.getText();
                Ollama ollama = new Ollama("llama3.1", message);

                // Modifiez votre service pour retourner la réponse
                String response = ollama.generate();

                // Mettre à jour l'UI sur le thread JavaFX
                javafx.application.Platform.runLater(() -> {
                    responseArea.appendText("Assistant: " + response + "\n\n");
                    messageField.clear();
                    sendButton.setDisable(false);
                    // Auto-scroll
                    responseArea.setScrollTop(Double.MAX_VALUE);
                });

            } catch (IOException e) {
                javafx.application.Platform.runLater(() -> {
                    responseArea.appendText("Erreur: " + e.getMessage() + "\n\n");
                    sendButton.setDisable(false);
                });
                e.printStackTrace();
            }
        }).start();
    }

    public void navDashboard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Affichage.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    public void navClient(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/front.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}