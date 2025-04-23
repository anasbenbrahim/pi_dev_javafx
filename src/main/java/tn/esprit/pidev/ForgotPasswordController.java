package tn.esprit.pidev;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.pidev.Service.UserDAO;

import java.io.IOException;

public class ForgotPasswordController {

    @FXML
    private TextField emailField;

    @FXML
    private Label messageLabel;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void sendVerificationCode(ActionEvent event) {
        String email = emailField.getText().trim();

        // Basic validation
        if (email.isEmpty()) {
            messageLabel.setText("Please enter your email");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Check if email exists in the database
        if (!userDAO.checkEmailExists(email)) {
            messageLabel.setText("Email not found in our records");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Send verification code
        String code = EmailService.sendVerificationCode(email);

        if (code != null) {
            messageLabel.setText("Verification code sent! Check your email.");
            messageLabel.setStyle("-fx-text-fill: green;");

            // Navigate to reset password page
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ResetPassword.fxml"));
                Parent root = loader.load();

                // Pass email and code to the reset password controller
                ResetPasswordController resetController = loader.getController();
                resetController.setEmailAndCode(email, code);

                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                messageLabel.setText("Error loading reset password page");
                messageLabel.setStyle("-fx-text-fill: red;");
                e.printStackTrace();
            }
        } else {
            messageLabel.setText("Failed to send verification code. Please try again.");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void backToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            messageLabel.setText("Error loading login page");
            messageLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }
}