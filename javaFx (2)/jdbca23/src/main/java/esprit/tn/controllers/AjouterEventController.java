package esprit.tn.controllers;

import esprit.tn.entities.Event;
import esprit.tn.main.DatabaseConnection;
import esprit.tn.services.EventService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class AjouterEventController {

    @FXML private TextField nomField;
    @FXML private TextArea descrField;
    @FXML private DatePicker dateField;
    @FXML private ComboBox<String> typeField;
    @FXML private Label errorLabel;
    @FXML private Label nomFichierLabel;
    @FXML private ImageView imagePreview;

    private EventService eventService;
    private File fichierImage;
    private static final String UPLOAD_DIR = "uploads/"; // Dossier où sauvegarder les images

    @FXML
    public void initialize() {
        try {
            eventService = new EventService(DatabaseConnection.getInstance().getCnx());
            // Créer le dossier uploads s'il n'existe pas
            new File(UPLOAD_DIR).mkdirs();
            // Ajout des types dans le ComboBox
            typeField.getItems().addAll("festival", "workshop", "evenement");
        } catch (Exception e) {
            showErrorAlert("Database Error", "Failed to initialize: " + e.getMessage());
        }
    }

    @FXML
    void choisirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");

        // Filtre pour les fichiers image
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Images", "*.jpg", "*.jpeg", "*.png", "*.gif");
        fileChooser.getExtensionFilters().add(extFilter);

        fichierImage = fileChooser.showOpenDialog(new Stage());
        if (fichierImage != null) {
            nomFichierLabel.setText(fichierImage.getName());

            // Afficher un aperçu de l'image
            Image image = new Image(fichierImage.toURI().toString());
            imagePreview.setImage(image);
        }
    }

    @FXML
    void ajouterEvent(ActionEvent event) {
        // Validation des champs
        if (nomField.getText().isEmpty() || descrField.getText().isEmpty()
                || dateField.getValue() == null || typeField.getValue() == null || typeField.getValue().isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs obligatoires!");
            return;
        }

        if (fichierImage == null) {
            errorLabel.setText("Veuillez sélectionner une image!");
            return;
        }

        // Convertir LocalDate en java.sql.Date
        LocalDate localDate = dateField.getValue();
        Date sqlDate = Date.valueOf(localDate);

        try {
            // Copier le fichier dans le dossier uploads
            String nomFichier = System.currentTimeMillis() + "_" + fichierImage.getName();
            Path destination = Path.of(UPLOAD_DIR + nomFichier);
            Files.copy(fichierImage.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

            // Créer le nouvel événement
            Event evt = new Event();
            evt.setNom(nomField.getText());
            evt.setDescr(descrField.getText());
            evt.setDate(sqlDate);
            evt.setType(typeField.getValue());
            evt.setPhoto(destination.toString()); // Chemin du fichier sauvegardé

            // Ajouter l'événement
            eventService.ajouter(evt);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Événement ajouté avec succès!");
            alert.showAndWait();

            clearFields();

        } catch (IOException e) {
            errorLabel.setText("Erreur lors de la copie de l'image: " + e.getMessage());
            showErrorAlert("Erreur de fichier", e.getMessage());
        } catch (SQLException e) {
            errorLabel.setText("Erreur lors de l'ajout: " + e.getMessage());
            showErrorAlert("Erreur de base de données", e.getMessage());
        }
    }

    @FXML
    void afficherListe(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherEvent.fxml"));
            Stage stage = (Stage) nomField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Erreur de navigation", "Impossible d'afficher la liste des événements: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nomField.clear();
        descrField.clear();
        dateField.setValue(null);
        typeField.getSelectionModel().clearSelection();
        errorLabel.setText("");
        nomFichierLabel.setText("Aucun fichier sélectionné");
        imagePreview.setImage(null);
        fichierImage = null;
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}