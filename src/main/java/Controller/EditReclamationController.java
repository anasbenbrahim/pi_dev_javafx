package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import modele.Publication;
import modele.Reclamation;
import services.ServicePublication;
import services.ServiceReclamation;

public class EditReclamationController {
    @FXML private Label publicationLabel;
    @FXML private TextField titreField;
    @FXML private TextArea descriptionField;
    @FXML private ChoiceBox<String> statusChoiceBox;
    @FXML private Button saveBtn;

    private Reclamation reclamation;
    private Runnable onReclamationUpdated;
    private final ServiceReclamation reclamationService = new ServiceReclamation();
    private final ServicePublication publicationService = new ServicePublication();

    public void setReclamation(Reclamation reclamation) {
        this.reclamation = reclamation;
        initializeFields();
    }

    public void setOnReclamationUpdated(Runnable callback) {
        this.onReclamationUpdated = callback;
    }

    @FXML
    public void initialize() {
        statusChoiceBox.getItems().addAll("Nouveau", "En cours", "Résolu", "Rejeté");

        saveBtn.setOnAction(event -> saveReclamation());
    }

    private void initializeFields() {
        Publication pub = publicationService.getById(reclamation.getPublicationId());
        publicationLabel.setText(pub != null ? pub.getTitre() : "Inconnu");
        titreField.setText(reclamation.getTitre());
        descriptionField.setText(reclamation.getDescription());
        statusChoiceBox.setValue(reclamation.getStatus());
    }

    private void saveReclamation() {
        reclamation.setTitre(titreField.getText());
        reclamation.setDescription(descriptionField.getText());
        reclamation.setStatus(statusChoiceBox.getValue());

        try {
            reclamationService.update(reclamation);
            if (onReclamationUpdated != null) {
                onReclamationUpdated.run();
            }
            ((Stage) saveBtn.getScene().getWindow()).close();
        } catch (Exception e) {
            showAlert("Erreur", "Échec de la mise à jour de la réclamation");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}