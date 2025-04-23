package tn.esprit.pidev;



import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javafx.scene.image.ImageView;
import tn.esprit.pidev.Model.User;
import tn.esprit.pidev.Service.UserSession;


import java.text.SimpleDateFormat;

public class ProfileController {

    @FXML private ImageView profileImageView;
    @FXML private Label welcomeText;
    @FXML private Label fullNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label roleLabel;
    @FXML private Label addressLabel;
    @FXML private Label phoneLabel;
    @FXML private Label birthDateLabel;
    @FXML private Label statusLabel;

    private User currentUser;

    @FXML
    public void initialize() {
        loadUserData();
    }

    private void loadUserData() {
        currentUser = UserSession.getInstance().getUser();

        if (currentUser != null) {


            // Set user information
            welcomeText.setText("Bienvenue, " + currentUser.getFirstName() + "!");
            fullNameLabel.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
            emailLabel.setText(currentUser.getEmail());
            roleLabel.setText(capitalizeFirstLetter(currentUser.getFirstName()) + " " + capitalizeFirstLetter(currentUser.getLastName()));
            addressLabel.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "Non spécifiée");
            phoneLabel.setText(currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "Non spécifié");

            // Format birth date
            if (currentUser.getBirthDate() != null) {
                birthDateLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(currentUser.getBirthDate()));
            } else {
                birthDateLabel.setText("Non spécifiée");
            }
        }
    }

    @FXML
    private void handleEditProfile() {
        // Logic to open edit profile dialog
        statusLabel.setText("Fonctionnalité de modification en développement");
    }

    @FXML
    private void handleChangePassword() {
        // Logic to open change password dialog
        statusLabel.setText("Fonctionnalité de changement de mot de passe en développement");
    }




    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}