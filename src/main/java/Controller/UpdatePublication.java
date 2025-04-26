package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import modele.Publication;
import services.ServicePublication;

import java.io.File;
import java.time.LocalDate;

public class UpdatePublication {

    @FXML private TextArea descriptionupdate;
    @FXML private TextField imageupdate;
    @FXML private TextField titreupdate;
    @FXML private Button chooseImageButton;
    @FXML private Button retourButton;

    private Publication publication;
    private final ServicePublication service = new ServicePublication();
    private File selectedImageFile;
    private NavigationManager navigationManager;
    private Runnable refreshCallback;

    public void setPublication(Publication publication) {
        this.publication = publication;
        if (publication != null && titreupdate != null) {
            titreupdate.setText(publication.getTitre());
            descriptionupdate.setText(publication.getDescription());
            imageupdate.setText(publication.getImageUrl());
        }
    }

    public void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }

    public void setRefreshCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    @FXML
    public void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        selectedImageFile = fileChooser.showOpenDialog(navigationManager.getPrimaryStage());
        if (selectedImageFile != null) {
            imageupdate.setText(selectedImageFile.getAbsolutePath());
        }
    }

    @FXML
    public void updatePublication() {
        String titre = titreupdate.getText();
        String description = descriptionupdate.getText();
        String imagePath = imageupdate.getText();

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
            showAlert("Erreur", "L'URL de l'image ne peut pas être vide.", Alert.AlertType.ERROR);
            return;
        }

        publication.setTitre(titre);
        publication.setDescription(description);
        if (selectedImageFile != null && selectedImageFile.exists()) {
            publication.setImageUrl(selectedImageFile.getAbsolutePath());
        } else {
            publication.setImageUrl(imagePath);
        }
        publication.setDate(LocalDate.now());

        try {
            service.update(publication);
            showAlert("Succès", "Publication mise à jour avec succès !", Alert.AlertType.INFORMATION);
            if (refreshCallback != null) {
                System.out.println("Triggering refresh callback from UpdatePublication");
                refreshCallback.run();
            }
            navigationManager.goBack();
        } catch (Exception e) {
            showAlert("Erreur", "Échec de la mise à jour de la publication : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void goBack() {
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

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}