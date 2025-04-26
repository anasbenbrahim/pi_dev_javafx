package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import modele.Publication;
import modele.Reclamation;
import services.ServiceReclamation;
import java.time.LocalDate;

public class AddReclamationController {
    @FXML private TextField titreField;
    @FXML private TextArea descriptionField;
    @FXML private Button retourButton;

    private Publication publication;
    private Runnable onReclamationAdded;
    private final ServiceReclamation reclamationService = new ServiceReclamation();
    private NavigationManager navigationManager;

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    @FXML
    private void handleAddReclamation() {
        String titre = titreField.getText();
        String description = descriptionField.getText();

        // Validation
        if (!isValidTitre(titre)) {
            showAlert("Erreur", "Le titre doit contenir au moins 3 caractères et ne doit pas inclure de chiffres.", Alert.AlertType.ERROR);
            return;
        }
        if (!isValidDescription(description)) {
            showAlert("Erreur", "La description doit contenir au moins 3 caractères.", Alert.AlertType.ERROR);
            return;
        }

        Reclamation reclamation = new Reclamation(
                titre,
                description,
                publication.getId(),
                getLoggedInClientId(),
                LocalDate.now()
        );

        try {
            reclamationService.insert(reclamation);
            showAlert("Succès", "Réclamation soumise avec succès !", Alert.AlertType.INFORMATION);
            titreField.clear();
            descriptionField.clear();
            navigationManager.goBack();
        } catch (Exception e) {
            showAlert("Erreur", "Échec de la soumission de la réclamation : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void goBack() {
        navigationManager.goBack();
    }

    private boolean isValidTitre(String titre) {
        if (titre == null || titre.trim().length() < 3) {
            return false;
        }
        // No digits allowed
        return titre.matches("^[\\p{L}\\s\\p{P}]+$") && !titre.matches(".*\\d.*");
    }

    private boolean isValidDescription(String description) {
        return description != null && description.trim().length() >= 3;
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private int getLoggedInClientId() {
        return 1; // Placeholder
    }
}