package tn.esprit.pidev;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class ClientHomeController {

    @FXML
    private Button contactButton;
    @FXML
    private Button loginButton; // Ajout du bouton login

    @FXML
    private Button signupButton;

    @FXML
    public void initialize() {
        // Initialisation si nécessaire
    }

    @FXML
    private void navigerToLogin() {
        try {
            // Charger la page login
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Scene scene = new Scene(root);

            // Obtenir la scène actuelle
            Stage stage = (Stage) loginButton.getScene().getWindow();

            // Changer la scène
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la page login!");
        }
    }

    @FXML
    private void navigerToRegister() {
        try {
            // Charger la page login
            Parent root = FXMLLoader.load(getClass().getResource("signup.fxml"));
            Scene scene = new Scene(root);

            // Obtenir la scène actuelle
            Stage stage = (Stage) signupButton.getScene().getWindow();

            // Changer la scène
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la page signup!");
        }
    }
}