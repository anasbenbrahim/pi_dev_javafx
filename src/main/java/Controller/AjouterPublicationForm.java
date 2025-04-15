package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modele.Publication;
import services.ServicePublication;

import java.io.IOException;
import java.time.LocalDate;

public class AjouterPublicationForm {

    @FXML
    private TextField titreField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField imageUrlField;

    @FXML
    private Button submitButton;

    private final ServicePublication service = new ServicePublication();

    @FXML
    void onSubmit(ActionEvent event) {
        String titre = titreField.getText();
        String description = descriptionField.getText();
        LocalDate date = datePicker.getValue();
        String imageUrl = imageUrlField.getText();

        if (titre.isEmpty() || description.isEmpty() || date == null || imageUrl.isEmpty()) {
            showAlert("Please fill in all fields.");
            return;
        }

        Publication publication = new Publication(titre, description, date, imageUrl);
        service.insert(publication);

        clearForm();
        showAlert("Publication added!");
    }

    @FXML
    void onViewPublications(ActionEvent event) {
        try {
            // Load the FXML for the 'AfficherPublication' scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherPublication.fxml"));
            Scene scene = new Scene(loader.load());

            // Get the stage (window) to switch the scene
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error loading publication list.");
        }
    }

    private void clearForm() {
        titreField.clear();
        descriptionField.clear();
        datePicker.setValue(null);
        imageUrlField.clear();
    }

    private void showAlert(String message) {
        new Alert(Alert.AlertType.INFORMATION, message).show();
    }
}
