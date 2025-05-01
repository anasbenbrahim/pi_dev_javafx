package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import modele.Publication;
import modele.Reclamation;
import services.ServicePublication;
import services.ServiceReclamation;
import java.time.LocalDate;

public class EditReclamationController {
    @FXML private Label publicationLabel;
    @FXML private TextField titreField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker dateField;
    @FXML private Button saveBtn;
    @FXML private Button retourButton;

    private Reclamation reclamation;
    private Runnable onReclamationUpdated;
    private final ServiceReclamation reclamationService = new ServiceReclamation();
    private final ServicePublication publicationService = new ServicePublication();
    private NavigationManager navigationManager;

    public void setReclamation(Reclamation reclamation) {
        this.reclamation = reclamation;
        initializeFields();
    }

    public void setOnReclamationUpdated(Runnable callback) {
        this.onReclamationUpdated = callback;
    }

    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    @FXML
    public void initialize() {
        saveBtn.setOnAction(event -> saveReclamation());
    }

    private void initializeFields() {
        Publication pub = publicationService.getById(reclamation.getPublicationId());
        publicationLabel.setText(pub != null ? pub.getTitre() : "Inconnu");
        titreField.setText(reclamation.getTitre());
        descriptionField.setText(reclamation.getDescription());
        dateField.setValue(reclamation.getDate());
    }

    private void saveReclamation() {
        String titre = titreField.getText();
        String description = descriptionField.getText();
        LocalDate date = dateField.getValue();

        if (!isValidTitre(titre)) {
            showAlert("Erreur", "Le titre doit contenir au moins 3 caractères et ne doit pas inclure de chiffres.", Alert.AlertType.ERROR);
            return;
        }
        if (!isValidDescription(description)) {
            showAlert("Erreur", "La description doit contenir au moins 3 caractères.", Alert.AlertType.ERROR);
            return;
        }
        if (date == null || date.isBefore(LocalDate.now())) {
            showAlert("Erreur", "La date ne peut pas être antérieure à aujourd'hui.", Alert.AlertType.ERROR);
            return;
        }

        reclamation.setTitre(titre);
        reclamation.setDescription(description);
        reclamation.setDate(date);

        try {
            reclamationService.update(reclamation);
            showAlert("Succès", "Réclamation mise à jour avec succès !", Alert.AlertType.INFORMATION);
            if (onReclamationUpdated != null) {
                onReclamationUpdated.run();
            }
            navigationManager.goBack();
        } catch (Exception e) {
            showAlert("Erreur", "Échec de la mise à jour de la réclamation : " + e.getMessage(), Alert.AlertType.ERROR);
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
}