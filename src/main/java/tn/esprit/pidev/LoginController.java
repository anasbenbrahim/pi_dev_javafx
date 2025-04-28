package tn.esprit.pidev;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import tn.esprit.pidev.Model.User;
import tn.esprit.pidev.Service.UserDAO;

import java.io.IOException;
import java.util.Arrays;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private ImageView myImageView;

    @FXML
    private ImageView BgImageView;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Basic validation
        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        User authenticatedUser = userDAO.authenticateUser(email, password);

        if (authenticatedUser != null) {
            messageLabel.setText("Connexion réussie !");
            messageLabel.setStyle("-fx-text-fill: green;");

            // Navigate to home page based on primary role
            try {
                String fxmlFile = determineDashboardFile(authenticatedUser.getRoles());

                Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                messageLabel.setText("Erreur lors du chargement de la page");
                messageLabel.setStyle("-fx-text-fill: red;");
                e.printStackTrace();
            }
        } else {
            messageLabel.setText("Email ou mot de passe incorrect");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private String determineDashboardFile(String[] roles) {
        if (roles == null || roles.length == 0) {
            return "DefaultDashboard.fxml";
        }

        // Check roles in priority order
        if (containsRole(roles, "admin")) {
            return "AgriTechDashboard.fxml";
        }
        if (containsRole(roles, "fermier")) {
            return "FermierDashboard.fxml";
        }
        if (containsRole(roles, "fournisseur")) {
            return "FournisseurDashboard.fxml";
        }
        if (containsRole(roles, "client")) {
            return "ClientHome.fxml";
        }

        return "DefaultDashboard.fxml";
    }

    private boolean containsRole(String[] roles, String roleToFind) {
        return Arrays.stream(roles)
                .anyMatch(role -> role.equalsIgnoreCase(roleToFind));
    }

    @FXML
    private void goToSignup(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("signup.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            messageLabel.setText("Erreur lors du chargement de la page d'inscription");
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    @FXML
    private void forgotPassword(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("ForgotPassword.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            messageLabel.setText("Erreur lors du chargement de la page de récupération");
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }
}