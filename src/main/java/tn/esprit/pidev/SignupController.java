package tn.esprit.pidev;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.pidev.Model.User;
import tn.esprit.pidev.Service.UserDAO;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;
import java.util.ResourceBundle;

public class SignupController implements Initializable {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Label specialityLabel;

    @FXML
    private TextField specialityField;

    @FXML
    private TextField addressField;

    @FXML
    private DatePicker birthDatePicker;

    @FXML
    private TextField phoneField;

    @FXML
    private Label messageLabel;

    @FXML
    private VBox signupFormVBox;

    @FXML
    private VBox verificationVBox;

    @FXML
    private TextField verificationCodeField;

    @FXML
    private Label verificationMessageLabel;

    @FXML
    private ImageView myImageView;

    @FXML
    private ImageView BgImageView;

    private User pendingUser;
    private UserDAO userDAO = new UserDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Default role selection
        roleComboBox.setValue("Select Role");

        try {
            InputStream logoStream = getClass().getResourceAsStream("/tn/esprit/pidev/images/logo.png");
            if (logoStream != null) {
                Image image = new Image(logoStream);
                myImageView.setImage(image);
            } else {
                System.err.println("Could not find logo.png in resources");
            }

            InputStream bgStream = getClass().getResourceAsStream("/tn/esprit/pidev/images/bgimage.jpg");
            if (bgStream != null) {
                Image bg = new Image(bgStream);
                BgImageView.setImage(bg);
            } else {
                System.err.println("Could not find bg.jpg in resources");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Show/hide speciality field based on role selection
        roleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isAdmin = "admin".equals(newVal);
            specialityLabel.setVisible(isAdmin);
            specialityField.setVisible(isAdmin);
        });

        // Initially hide verification screen
        if (verificationVBox != null) {
            verificationVBox.setVisible(false);
            verificationVBox.setManaged(false);
        }
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        // Get input values
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        String role = roleComboBox.getValue();
        String speciality = specialityField.getText().trim();
        String address = addressField.getText().trim();
        LocalDate birthLocalDate = birthDatePicker.getValue();
        String phoneNumber = phoneField.getText().trim();

        // Validation
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty() || role == null || "Select Role".equals(role)) {
            messageLabel.setText("Please fill in all required fields");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            messageLabel.setText("Please enter a valid email address");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if ("admin".equals(role) && speciality.isEmpty()) {
            messageLabel.setText("Please enter your speciality");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Check if email already exists
        if (userDAO.emailExists(email)) {
            messageLabel.setText("Email already registered");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Create user object with roles array
        User newUser = new User();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setRoles(new String[]{role}); // Set roles as array
        newUser.setSpecialite(speciality);
        newUser.setAddress(address);

        if (birthLocalDate != null) {
            newUser.setBirthDate(Date.valueOf(birthLocalDate));
        }

        newUser.setPhoneNumber(phoneNumber);

        // Store the user temporarily
        this.pendingUser = newUser;

        // Send verification code
        String code = EmailService.sendVerificationCode(email);
        if (code == null) {
            messageLabel.setText("Failed to send verification code. Please try again.");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Show verification screen
        messageLabel.setText("Verification code sent to your email!");
        messageLabel.setStyle("-fx-text-fill: green;");

        signupFormVBox.setVisible(false);
        signupFormVBox.setManaged(false);
        verificationVBox.setVisible(true);
        verificationVBox.setManaged(true);
    }

    @FXML
    private void handleVerification(ActionEvent event) {
        String code = verificationCodeField.getText().trim();

        if (code.isEmpty()) {
            verificationMessageLabel.setText("Please enter the verification code");
            verificationMessageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (EmailService.verifyCode(pendingUser.getEmail(), code)) {
            // Code is correct, create the account
            boolean success = userDAO.addUser(pendingUser);

            if (success) {
                verificationMessageLabel.setText("Registration successful! Redirecting to login...");
                verificationMessageLabel.setStyle("-fx-text-fill: green;");

                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(() -> {
                            try {
                                Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
                                Scene scene = new Scene(root);
                                Stage stage = (Stage) verificationMessageLabel.getScene().getWindow();
                                stage.setScene(scene);
                                stage.show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                verificationMessageLabel.setText("Registration failed. Please try again.");
                verificationMessageLabel.setStyle("-fx-text-fill: red;");
            }
        } else {
            verificationMessageLabel.setText("Invalid or expired verification code");
            verificationMessageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void resendVerificationCode(ActionEvent event) {
        if (pendingUser != null) {
            String code = EmailService.sendVerificationCode(pendingUser.getEmail());
            if (code != null) {
                verificationMessageLabel.setText("New code sent! Please check your email");
                verificationMessageLabel.setStyle("-fx-text-fill: green;");
            } else {
                verificationMessageLabel.setText("Failed to send new code");
                verificationMessageLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }

    @FXML
    private void backToSignup(ActionEvent event) {
        verificationVBox.setVisible(false);
        verificationVBox.setManaged(false);
        signupFormVBox.setVisible(true);
        signupFormVBox.setManaged(true);
    }

    @FXML
    private void goToLogin(ActionEvent event) {
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