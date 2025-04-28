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
import javafx.stage.Stage;
import tn.esprit.pidev.Service.UserDAO;

import java.io.IOException;

public class ResetPasswordController {

    @FXML
    private TextField codeField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    private String email;
    private String sentCode;
    private UserDAO userDAO = new UserDAO();

    /**
     * Set the email and verification code received from the ForgotPasswordController
     */
    public void setEmailAndCode(String email, String code) {
        this.email = email;
        this.sentCode = code;
    }

    @FXML
    private void resetPassword(ActionEvent event) {
        String code = codeField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // Basic validation
        if (code.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("Please fill in all fields");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Verify the code
        if (!EmailService.verifyCode(email, code)) {
            messageLabel.setText("Invalid or expired verification code");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Update the password
        boolean success = userDAO.updatePassword(email, newPassword);

        if (success) {
            messageLabel.setText("Password reset successful!");
            messageLabel.setStyle("-fx-text-fill: green;");

            // Return to login page after a short delay
            javafx.application.Platform.runLater(() -> {
                try {
                    Thread.sleep(2000);
                    Parent root = FXMLLoader.load(getClass().getResource("LoginView.fxml"));
                    Scene scene = new Scene(root);
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                    stage.setScene(scene);
                    stage.show();
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            messageLabel.setText("Failed to update password. Please try again.");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void backToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("LoginView.fxml"));
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