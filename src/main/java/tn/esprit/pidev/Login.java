package tn.esprit.pidev;




import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import tn.esprit.pidev.Model.User;
import tn.esprit.pidev.Service.UserDAO;

public class Login implements Initializable {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField email1Field;

    @FXML
    private PasswordField password1Field;

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



    private User pendingUser;




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
    private TextField emailField;

    @FXML
    private PasswordField passwordField;





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

    @FXML
    private void home(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("ClientHome.fxml"));
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














    @FXML private Button loginTab;
    @FXML private Button signupTab;
    @FXML private VBox loginForm;
    @FXML private VBox signupForm;

    // Login controls
    @FXML private TextField loginEmail;
    @FXML private PasswordField loginPassword;
    @FXML private CheckBox rememberMe;
    @FXML private Button loginShowPassword;
    private TextField loginPasswordVisible;

    // Signup controls
    @FXML private TextField signupName;
    @FXML private TextField signupEmail;
    @FXML private PasswordField signupPassword;
    @FXML private PasswordField signupConfirmPassword;
    @FXML private Button signupShowPassword;
    private TextField signupPasswordVisible;
    @FXML private ProgressBar passwordStrength;
    @FXML private Label passwordStrengthText;
    @FXML private CheckBox termsCheckbox;

    // Flag to track password visibility
    private boolean loginPasswordShown = false;
    private boolean signupPasswordShown = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {



        roleComboBox.setValue("Select Role");

        try {
            InputStream logoStream = getClass().getResourceAsStream("/tn/esprit/pidev/images/logo.png");
            if (logoStream != null) {
                Image image = new Image(logoStream);

            } else {
                System.err.println("Could not find logo.png in resources");
            }

            InputStream bgStream = getClass().getResourceAsStream("/tn/esprit/pidev/images/bgimage.jpg");
            if (bgStream != null) {

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











        // Initialize visible password text fields
        loginPasswordVisible = new TextField();
        loginPasswordVisible.setPromptText("Votre mot de passe");
        loginPasswordVisible.setVisible(false);
        loginPasswordVisible.setManaged(false);

        signupPasswordVisible = new TextField();
        signupPasswordVisible.setPromptText("Votre mot de passe");
        signupPasswordVisible.setVisible(false);
        signupPasswordVisible.setManaged(false);

        // Add them to the scene graph next to the password fields


        // Initialize password strength indicator
        passwordStrength.setProgress(0);
        passwordStrength.getStyleClass().add("password-strength-indicator");
    }

    @FXML
    private void showLoginForm() {
        loginForm.setVisible(true);
        loginForm.setManaged(true);
        signupForm.setVisible(false);
        signupForm.setManaged(false);

        loginTab.getStyleClass().add("tab-active");
        signupTab.getStyleClass().remove("tab-active");
    }

    @FXML
    private void showSignupForm() {
        loginForm.setVisible(false);
        loginForm.setManaged(false);
        signupForm.setVisible(true);
        signupForm.setManaged(true);

        signupTab.getStyleClass().add("tab-active");
        loginTab.getStyleClass().remove("tab-active");
    }

    @FXML
    private void toggleLoginPassword() {
        loginPasswordShown = !loginPasswordShown;

        if (loginPasswordShown) {
            loginPasswordVisible.setText(loginPassword.getText());
            loginPasswordVisible.setVisible(true);
            loginPasswordVisible.setManaged(true);
            loginPassword.setVisible(false);
            loginPassword.setManaged(false);
            loginShowPassword.setText("👁️‍🗨️");
        } else {
            loginPassword.setText(loginPasswordVisible.getText());
            loginPassword.setVisible(true);
            loginPassword.setManaged(true);
            loginPasswordVisible.setVisible(false);
            loginPasswordVisible.setManaged(false);
            loginShowPassword.setText("👁️");
        }
    }

    @FXML
    private void toggleSignupPassword() {
        signupPasswordShown = !signupPasswordShown;

        if (signupPasswordShown) {
            signupPasswordVisible.setText(signupPassword.getText());
            signupPasswordVisible.setVisible(true);
            signupPasswordVisible.setManaged(true);
            signupPassword.setVisible(false);
            signupPassword.setManaged(false);
            signupShowPassword.setText("👁️‍🗨️");
        } else {
            signupPassword.setText(signupPasswordVisible.getText());
            signupPassword.setVisible(true);
            signupPassword.setManaged(true);
            signupPasswordVisible.setVisible(false);
            signupPasswordVisible.setManaged(false);
            signupShowPassword.setText("👁️");
        }
    }

    @FXML
    private void updatePasswordStrength() {
        String password = signupPassword.getText();
        int strength = 0;

        if (password.length() >= 8) strength += 1;
        if (password.matches(".*[a-z].*") && password.matches(".*[A-Z].*")) strength += 1;
        if (password.matches(".*\\d.*")) strength += 1;
        if (password.matches(".*[^a-zA-Z0-9].*")) strength += 1;

        // Update progress bar
        double progressValue = strength / 4.0;
        passwordStrength.setProgress(progressValue);

        // Update style class based on strength
        passwordStrength.getStyleClass().removeAll("password-weak", "password-medium", "password-strong");

        switch (strength) {
            case 0:
                passwordStrengthText.setText("Utilisez au moins 8 caractères avec des lettres et des chiffres");
                passwordStrengthText.setTextFill(Color.GRAY);
                break;
            case 1:
                passwordStrength.getStyleClass().add("password-weak");
                passwordStrengthText.setText("Mot de passe faible");
                passwordStrengthText.setTextFill(Color.RED);
                break;
            case 2:
            case 3:
                passwordStrength.getStyleClass().add("password-medium");
                passwordStrengthText.setText("Mot de passe moyen");
                passwordStrengthText.setTextFill(Color.ORANGE);
                break;
            case 4:
                passwordStrength.getStyleClass().add("password-strong");
                passwordStrengthText.setText("Mot de passe fort");
                passwordStrengthText.setTextFill(Color.GREEN);
                break;
        }
    }

    @FXML
    private void login(ActionEvent event) {
        String email = loginEmail.getText();
        String password = loginPasswordShown ? loginPasswordVisible.getText() : loginPassword.getText();
        boolean remember = rememberMe.isSelected();

        // TODO: Implement authentication logic
        System.out.println("Login attempt: " + email + ", Remember: " + remember);
    }

    @FXML
    private void register(ActionEvent event) {
        String name = signupName.getText();
        String email = signupEmail.getText();
        String password = signupPasswordShown ? signupPasswordVisible.getText() : signupPassword.getText();
        String confirmPassword = signupConfirmPassword.getText();
        boolean terms = termsCheckbox.isSelected();

        // Validate inputs
        if (!password.equals(confirmPassword)) {
            // Show error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur d'inscription");
            alert.setHeaderText("Les mots de passe ne correspondent pas");
            alert.setContentText("Veuillez vérifier que les deux mots de passe sont identiques.");
            alert.showAndWait();
            return;
        }

        if (!terms) {
            // Show error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur d'inscription");
            alert.setHeaderText("Conditions d'utilisation non acceptées");
            alert.setContentText("Veuillez accepter les conditions d'utilisation pour continuer.");
            alert.showAndWait();
            return;
        }

        // TODO: Implement registration logic
        System.out.println("Registration attempt: " + name + ", " + email);
    }

    @FXML
    private void loginWithGoogle() {
        // TODO: Implement Google authentication
        System.out.println("Login with Google");
    }

    @FXML
    private void loginWithFacebook() {
        // TODO: Implement Facebook authentication
        System.out.println("Login with Facebook");
    }

    @FXML
    private void signupWithGoogle() {
        // TODO: Implement Google signup
        System.out.println("Signup with Google");
    }

    @FXML
    private void signupWithFacebook() {
        // TODO: Implement Facebook signup
        System.out.println("Signup with Facebook");
    }

    @FXML
    private void forgotPassword() {
        // TODO: Implement forgot password functionality
        System.out.println("Forgot password");
    }

    @FXML
    private void showTerms() {
        // TODO: Show terms and conditions
        System.out.println("Show terms and conditions");
    }

    @FXML
    private void showPrivacyPolicy() {
        // TODO: Show privacy policy
        System.out.println("Show privacy policy");
    }
}