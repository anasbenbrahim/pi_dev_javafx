package esprit.tn.controllers;

import esprit.tn.entities.Event;
import esprit.tn.entities.Reservation;
import esprit.tn.main.DatabaseConnection;
import esprit.tn.services.ReservationService;
import esprit.tn.utils.QRAndPDFUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

public class AjouterReservationController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private DatePicker dateField;

    @FXML
    private Label eventLabel;

    private Event selectedEvent;
    private final ReservationService reservationService;

    public AjouterReservationController() {
        this.reservationService = new ReservationService(DatabaseConnection.getInstance().getCnx());
    }

    public void initData(Event event) {
        this.selectedEvent = event;
        if (eventLabel != null) {
            eventLabel.setText("Réservation pour: " + event.getNom());
        }
        if (dateField != null) {
            dateField.setValue(event.getDate().toLocalDate());
        }
    }

    @FXML
    private void ajouterReservation() {
        if (validateInputs()) {
            try {
                Reservation reservation = new Reservation(
                    nomField.getText(),
                    prenomField.getText(),
                    Date.valueOf(dateField.getValue()),
                    selectedEvent.getId()
                );

                reservationService.ajouter(reservation);

                // Génération du PDF et du QR code
                String pdfPath = "invitation_" + nomField.getText() + "_" + prenomField.getText() + ".pdf";
                String qrPath = "qr_" + nomField.getText() + "_" + prenomField.getText() + ".png";
                String eventName = selectedEvent.getNom();
                String eventDate = dateField.getValue().toString();
                QRAndPDFUtil.generatePDFInvitation(nomField.getText(), prenomField.getText(), eventName, eventDate, pdfPath);
                // QR code encode le chemin absolu du PDF
                QRAndPDFUtil.generateQRCode(new java.io.File(pdfPath).getAbsolutePath(), qrPath, 300, 300);

                showAlert("Succès", "Réservation ajoutée avec succès et invitation générée !", Alert.AlertType.INFORMATION);
                retourVersEvent();
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de l'ajout: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private boolean validateInputs() {
        if (selectedEvent == null) {
            showAlert("Erreur", "Aucun événement sélectionné", Alert.AlertType.ERROR);
            return false;
        }
        if (nomField.getText().isEmpty()) {
            showAlert("Erreur", "Le nom est requis", Alert.AlertType.ERROR);
            return false;
        }
        if (prenomField.getText().isEmpty()) {
            showAlert("Erreur", "Le prénom est requis", Alert.AlertType.ERROR);
            return false;
        }
        if (dateField.getValue() == null) {
            showAlert("Erreur", "La date est requise", Alert.AlertType.ERROR);
            return false;
        }
        if (dateField.getValue().isBefore(LocalDate.now())) {
            showAlert("Erreur", "La date ne peut pas être dans le passé", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    @FXML
    private void annuler() {
        retourVersEvent();
    }

    private void retourVersEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affichageEventfront.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nomField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur de navigation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
