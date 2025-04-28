package esprit.tn.controllers;

import esprit.tn.entities.Event;
import esprit.tn.main.DatabaseConnection;
import esprit.tn.services.EventService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.Date;
import java.sql.SQLException;

public class ModifierEventController {
    @FXML private TextField nomField;
    @FXML private TextArea descrField;
    @FXML private DatePicker dateField;
    @FXML private TextField typeField;
    @FXML private TextField photoField;
    @FXML private Label errorLabel;

    private EventService eventService;
    private Event eventToModify;

    @FXML
    public void initialize() {
        eventService = new EventService(DatabaseConnection.getInstance().getCnx());
    }

    public void initData(Event event) {
        this.eventToModify = event;
        nomField.setText(event.getNom());
        descrField.setText(event.getDescr());
        dateField.setValue(event.getDate() != null ? event.getDate().toLocalDate() : null);
        typeField.setText(event.getType());
        photoField.setText(event.getPhoto());
    }

    @FXML
    private void modifierEvent() {
        if (!validateInputs()) {
            return;
        }

        try {
            updateEventFromFields();
            eventService.modifier(eventToModify);
            showAlert("Succès", "Événement modifié avec succès", Alert.AlertType.INFORMATION);
            closeWindow();
        } catch (SQLException e) {
            errorLabel.setText("Erreur lors de la modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();

        if (nomField.getText().trim().isEmpty()) {
            errors.append("Le nom est requis.\n");
        }
        if (descrField.getText().trim().isEmpty()) {
            errors.append("La description est requise.\n");
        }
        if (dateField.getValue() == null) {
            errors.append("La date est requise.\n");
        }
        if (typeField.getText().trim().isEmpty()) {
            errors.append("Le type est requis.\n");
        }

        if (errors.length() > 0) {
            errorLabel.setText(errors.toString());
            return false;
        }

        errorLabel.setText("");
        return true;
    }

    private void updateEventFromFields() {
        eventToModify.setNom(nomField.getText().trim());
        eventToModify.setDescr(descrField.getText().trim());
        eventToModify.setDate(Date.valueOf(dateField.getValue()));
        eventToModify.setType(typeField.getText().trim());
        eventToModify.setPhoto(photoField.getText().trim());
    }

    private void closeWindow() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}