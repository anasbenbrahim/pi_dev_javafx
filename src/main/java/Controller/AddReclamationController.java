package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import modele.Publication;
import modele.Reclamation;
import services.ServiceReclamation;

public class AddReclamationController {
    @FXML
    private TextField titreField;

    @FXML
    private TextArea descriptionField;

    private Publication publication;
    private Runnable onReclamationAdded;
    private final ServiceReclamation reclamationService = new ServiceReclamation();

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    @FXML
    private void handleAddReclamation() {
        String titre = titreField.getText();
        String description = descriptionField.getText();

        if (titre.isEmpty() || description.isEmpty()) {
            showAlert("Error", "Titre and description cannot be empty");
            return;
        }

        Reclamation reclamation = new Reclamation(
                titre,
                description,
                publication.getId(),
                getLoggedInClientId() // Replace with your logic
        );

        reclamationService.insert(reclamation);
        showAlert("Success", "Reclamation submitted!");

        // Clear fields
        titreField.clear();
        descriptionField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private int getLoggedInClientId() {
        return 1; // Placeholder
    }
}