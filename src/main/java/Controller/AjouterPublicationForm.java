package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import modele.Publication;
import services.ServicePublication;

import java.io.File;
import java.time.LocalDate;

public class AjouterPublicationForm {

    @FXML private TextField titreField;
    @FXML private TextArea descriptionField;
    @FXML private TextField imageUrlField;
    @FXML private Button chooseImageButton;
    @FXML private Button submitButton;
    @FXML private Button retourButton;

    private final ServicePublication service = new ServicePublication();
    private File selectedImageFile;
    private NavigationManager navigationManager;
    private Runnable refreshCallback;

    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    public void setRefreshCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    @FXML
    void chooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        selectedImageFile = fileChooser.showOpenDialog(navigationManager.getPrimaryStage());
        if (selectedImageFile != null) {
            imageUrlField.setText(selectedImageFile.getAbsolutePath());
        }
    }

    @FXML
    void onSubmit(ActionEvent event) {
        String titre = titreField.getText();
        String description = descriptionField.getText();
        String imagePath = imageUrlField.getText();

        // Validation
        if (!isValidTitre(titre)) {
            showAlert("Erreur", "Le titre doit contenir au moins 3 caractères et ne doit pas inclure de chiffres.", Alert.AlertType.ERROR);
            return;
        }
        if (!isValidDescription(description)) {
            showAlert("Erreur", "La description doit contenir au moins 3 caractères.", Alert.AlertType.ERROR);
            return;
        }
        if (imagePath.isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner une image valide.", Alert.AlertType.ERROR);
            return;
        }
        if (selectedImageFile == null || !selectedImageFile.exists()) {
            showAlert("Erreur", "Veuillez sélectionner un fichier image valide.", Alert.AlertType.ERROR);
            return;
        }

        Publication publication = new Publication(titre, description, LocalDate.now(), selectedImageFile.getAbsolutePath());
        service.insert(publication);

        clearForm();
        showAlert("Succès", "Publication ajoutée avec succès !", Alert.AlertType.INFORMATION);
        if (refreshCallback != null) {
            System.out.println("Triggering refresh callback from AjouterPublicationForm");
            refreshCallback.run();
        }
        navigationManager.goBack();
    }

    @FXML
    void goBack() {
        navigationManager.goBack();
    }

    private boolean isValidTitre(String titre) {
        if (titre == null || titre.trim().length() < 3) {
            return false;
        }
        return titre.matches("^[\\p{L}\\s\\p{P}]+$") && !titre.matches(".*\\d.*");
    }

    private boolean isValidDescription(String description) {
        return description != null && description.trim().length() >= 3;
    }

    private void clearForm() {
        titreField.clear();
        descriptionField.clear();
        imageUrlField.clear();
        selectedImageFile = null;
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}