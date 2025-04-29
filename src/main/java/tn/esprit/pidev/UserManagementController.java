package tn.esprit.pidev;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import tn.esprit.pidev.Model.User;
import tn.esprit.pidev.Service.TwilioService;
import tn.esprit.pidev.Service.UserDAO;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UserManagementController implements Initializable {

    @FXML private TextField idField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<String> genderCombo;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private TextField emailField;
    @FXML private FlowPane usersCardsContainer;
    @FXML private Label messageLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterRoleCombo;

    private ObservableList<User> usersList = FXCollections.observableArrayList();
    private UserDAO userDAO = new UserDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation des ComboBox
        genderCombo.setItems(FXCollections.observableArrayList("Homme", "Femme"));
        roleCombo.setItems(FXCollections.observableArrayList("admin", "fermier", "fournisseur", "client"));
        filterRoleCombo.setItems(FXCollections.observableArrayList("Tous", "admin", "fermier", "fournisseur", "client"));
        filterRoleCombo.setValue("Tous");

        // Configuration des écouteurs
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterUsers());
        filterRoleCombo.valueProperty().addListener((obs, oldVal, newVal) -> filterUsers());

        // Chargement initial des utilisateurs
        loadUsers();
    }

    public void loadUsers() {
        usersCardsContainer.getChildren().clear();
        usersList.clear();
        usersList.addAll(userDAO.getAllUsers());
        filterUsers();
    }

    private void filterUsers() {
        usersCardsContainer.getChildren().clear();

        String searchTerm = searchField.getText().toLowerCase();
        String selectedRole = filterRoleCombo.getValue();

        usersList.stream()
                .filter(user -> matchesSearch(user, searchTerm))
                .filter(user -> matchesRole(user, selectedRole))
                .forEach(user -> usersCardsContainer.getChildren().add(createUserCard(user)));
    }

    private boolean matchesSearch(User user, String searchTerm) {
        if (searchTerm.isEmpty()) return true;

        return user.getFirstName().toLowerCase().contains(searchTerm) ||
                user.getLastName().toLowerCase().contains(searchTerm) ||
                user.getEmail().toLowerCase().contains(searchTerm) ||
                user.getPhoneNumber().toLowerCase().contains(searchTerm) ||
                String.valueOf(user.getId()).contains(searchTerm);
    }

    private boolean matchesRole(User user, String selectedRole) {
        if (selectedRole == null || "Tous".equals(selectedRole)) return true;

        return Arrays.asList(user.getRoles()).contains(selectedRole);
    }

    private VBox createUserCard(User user) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #e0e0e0; -fx-border-radius: 5; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        card.setPrefWidth(250);

        // Informations utilisateur
        Label nameLabel = new Label(user.getFirstName() + " " + user.getLastName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label idLabel = new Label("ID: " + user.getId());
        Label emailLabel = new Label("Email: " + user.getEmail());
        Label roleLabel = new Label("Rôles: " + String.join(", ", user.getRoles()));
        Label phoneLabel = new Label("Tél: " + user.getPhoneNumber());
        Label statusLabel = new Label("Statut: " + (user.isBanned() ? "Banni" : "Actif"));
        statusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + (user.isBanned() ? "#dc3545" : "#28a745") + ";");

        // Boutons d'action
        HBox buttonsBox = new HBox(5);
        Button editBtn = new Button("Edit");
        editBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        editBtn.setOnAction(e -> fillFormWithUser(user));

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> deleteUser(user));

        // Bouton Ban/Unban dynamique
        Button banUnbanBtn;
        if (user.isBanned()) {
            banUnbanBtn = new Button("Unban");
            banUnbanBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
            banUnbanBtn.setOnAction(e -> unbanUser(user));
        } else {
            banUnbanBtn = new Button("Ban");
            banUnbanBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
            banUnbanBtn.setOnAction(e -> banUser(user));
        }

        buttonsBox.getChildren().addAll(editBtn, deleteBtn, banUnbanBtn);
        card.getChildren().addAll(nameLabel, idLabel, emailLabel, roleLabel, phoneLabel, statusLabel, buttonsBox);

        return card;
    }

    private void banUser(User user) {
        if (userDAO.banUser(user.getId())) {
            showMessage("Utilisateur banni avec succès", "green");
            // Recharger uniquement la carte de cet utilisateur au lieu de tout recharger
            refreshUserCard(user);
        } else {
            showMessage("Erreur lors du bannissement", "red");
        }
    }

    private void unbanUser(User user) {
        if (userDAO.unbanUser(user.getId())) {
            showMessage("Utilisateur débanni avec succès", "green");
            // Recharger uniquement la carte de cet utilisateur au lieu de tout recharger
            refreshUserCard(user);
        } else {
            showMessage("Erreur lors du débannissement", "red");
        }
    }

    // Méthode pour rafraîchir uniquement la carte d'un utilisateur spécifique
    private void refreshUserCard(User user) {
        // Trouver l'index de l'utilisateur dans la liste
        int index = usersList.indexOf(user);
        if (index >= 0) {
            // Mettre à jour l'utilisateur dans la liste
            usersList.set(index, userDAO.getUserById(user.getId()));
            // Recréer la carte
            usersCardsContainer.getChildren().set(index, createUserCard(usersList.get(index)));
        }
    }

    private void fillFormWithUser(User user) {
        idField.setText(String.valueOf(user.getId()));
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        genderCombo.setValue("Homme"); // Simplification
        phoneField.setText(user.getPhoneNumber());
        roleCombo.setValue(user.getRoles()[0]); // Prend le premier rôle
        emailField.setText(user.getEmail());
    }

    private void deleteUser(User user) {
        if (userDAO.deleteUser(user.getId())) {
            showMessage("Utilisateur supprimé avec succès", "green");
            loadUsers();
        } else {
            showMessage("Erreur lors de la suppression", "red");
        }
    }


    @FXML
    private void handleAdd() {
        if (validateForm()) {
            User user = createUserFromForm();
            String generatedPassword = generateRandomPassword();
            user.setPassword(generatedPassword);

            if (userDAO.addUser(user)) {
                // Envoi du mot de passe par SMS/WhatsApp et email
                try {
                    // Formatage du numéro avec l'indicatif +216
                    String formattedPhoneNumber = formatPhoneNumber(user.getPhoneNumber());

                    // Envoi par SMS
                    TwilioService.sendSMS(formattedPhoneNumber,
                            "Votre compte a été créé. Email: " + user.getEmail() +
                                    ", Mot de passe: " + generatedPassword);

                    showMessage("Utilisateur ajouté. Mot de passe envoyé.", "green");
                    loadUsers();
                    clearForm();
                } catch (Exception e) {
                    showMessage("Utilisateur ajouté mais échec d'envoi du mot de passe: " + e.getMessage(), "orange");
                }
            }
        }
    }

    // Méthode pour formater le numéro de téléphone
    private String formatPhoneNumber(String phoneNumber) {
        // Supprimer tous les caractères non numériques
        String digitsOnly = phoneNumber.replaceAll("[^0-9]", "");

        // Si le numéro commence déjà par 216, on ajoute juste le +
        if (digitsOnly.startsWith("216")) {
            return "+" + digitsOnly;
        }

        // Si le numéro a 8 chiffres (sans indicatif), on ajoute +216
        if (digitsOnly.length() == 8) {
            return "+216" + digitsOnly;
        }

        // Pour les autres formats, retourner le numéro original avec +216
        // (Vous pouvez ajouter d'autres règles de formatage si nécessaire)
        return "+216" + digitsOnly;
    }

    private void sendPasswordByWhatsApp(String phoneNumber, String password) {
        try {
            String formattedPhone = phoneNumber.replaceAll("[^0-9]", "");
            String message = "Votre nouveau mot de passe est: " + password;
            String encodedMessage = java.net.URLEncoder.encode(message, "UTF-8");
            String url = "https://wa.me/" + formattedPhone + "?text=" + encodedMessage;
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Erreur lors de l'envoi du message WhatsApp", "red");
        }
    }

    @FXML
    private void handleUpdate() {
        if (validateForm() && !idField.getText().isEmpty()) {
            User user = createUserFromForm();
            user.setId(Integer.parseInt(idField.getText()));

            if (userDAO.updateUser(user)) {
                showMessage("Utilisateur modifié avec succès", "green");
                loadUsers();
            }
        }
    }

    @FXML
    private void handleDelete() {
        if (!idField.getText().isEmpty()) {
            int id = Integer.parseInt(idField.getText());
            if (userDAO.deleteUser(id)) {
                showMessage("Utilisateur supprimé avec succès", "green");
                loadUsers();
                clearForm();
            }
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    private User createUserFromForm() {
        User user = new User();
        user.setFirstName(firstNameField.getText());
        user.setLastName(lastNameField.getText());
        user.setPhoneNumber(phoneField.getText());
        user.setRoles(new String[]{roleCombo.getValue()}); // Tableau de rôles
        user.setEmail(emailField.getText());
        return user;
    }

    private void clearForm() {
        idField.clear();
        firstNameField.clear();
        lastNameField.clear();
        genderCombo.getSelectionModel().clearSelection();
        phoneField.clear();
        roleCombo.getSelectionModel().clearSelection();
        emailField.clear();
    }

    private boolean validateForm() {
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() ||
                emailField.getText().isEmpty() || roleCombo.getValue() == null) {
            showMessage("Veuillez remplir tous les champs obligatoires", "red");
            return false;
        }
        return true;
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    private void showMessage(String message, String color) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + color + ";");
    }
}