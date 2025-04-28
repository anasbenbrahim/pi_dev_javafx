package esprit.tn.controllers;

import esprit.tn.entities.Event;
import esprit.tn.entities.Reservation;
import esprit.tn.main.DatabaseConnection;
import esprit.tn.services.EventService;
import esprit.tn.services.ReservationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class affichageEventfrontController {

    @FXML
    private FlowPane cardsContainer;
    
    @FXML
    private VBox formContainer;
    
    @FXML
    private Label selectedEventLabel;
    
    @FXML
    private TextField nomField;
    
    @FXML
    private TextField prenomField;
    
    @FXML
    private DatePicker dateField;

    private final EventService eventService;
    private final ReservationService reservationService;
    private Event selectedEvent;

    public affichageEventfrontController() {
        this.eventService = new EventService(DatabaseConnection.getInstance().getCnx());
        this.reservationService = new ReservationService(DatabaseConnection.getInstance().getCnx());
    }

    @FXML
    public void initialize() {
        try {
            formContainer.setVisible(false);
            loadEventCards();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'initialisation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadEventCards() {
        try {
            cardsContainer.getChildren().clear();
            for (Event event : eventService.getall()) {
                VBox card = createEventCard(event);
                cardsContainer.getChildren().add(card);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement des événements: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private VBox createEventCard(Event event) {
        VBox card = new VBox();
        card.setSpacing(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20;");
        card.setPrefWidth(300);
        
        // Ajout de l'effet d'ombre
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.1));
        dropShadow.setRadius(10);
        card.setEffect(dropShadow);
        
        // Effets de survol
        card.setOnMouseEntered(e -> {
            dropShadow.setRadius(15);
            dropShadow.setColor(Color.rgb(0, 0, 0, 0.2));
            card.setCursor(Cursor.HAND);
        });
        
        card.setOnMouseExited(e -> {
            dropShadow.setRadius(10);
            dropShadow.setColor(Color.rgb(0, 0, 0, 0.1));
        });
        
        card.setOnMouseClicked(e -> showReservationForm(event));

        ImageView imageView = new ImageView();
        imageView.setFitWidth(260);
        imageView.setFitHeight(150);
        imageView.setStyle("-fx-background-radius: 10;");

        try {
            if (event.getPhoto() != null && !event.getPhoto().isEmpty()) {
                File file = new File(event.getPhoto());
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    imageView.setImage(image);
                    imageView.setPreserveRatio(true);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'image: " + e.getMessage());
        }

        Label titleLabel = new Label(event.getNom());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        titleLabel.setWrapText(true);

        HBox typeBox = new HBox(10);
        Label typeIcon = new Label("🏷");
        Label typeLabel = new Label(event.getType());
        typeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        typeBox.getChildren().addAll(typeIcon, typeLabel);

        HBox dateBox = new HBox(10);
        Label dateIcon = new Label("📅");
        Label dateLabel = new Label(event.getDate().toString());
        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        dateBox.getChildren().addAll(dateIcon, dateLabel);

        Label descLabel = new Label(truncateText(event.getDescr(), 100));
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        descLabel.setWrapText(true);

        Button reserveButton = new Button("Réserver");
        reserveButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;");
        reserveButton.setPrefWidth(240);
        reserveButton.setOnAction(e -> showReservationForm(event));
        reserveButton.setCursor(Cursor.HAND);

        card.getChildren().addAll(imageView, titleLabel, typeBox, dateBox, descLabel, reserveButton);
        return card;
    }

    private void showReservationForm(Event event) {
        try {
            selectedEvent = event;
            selectedEventLabel.setText("Événement: " + event.getNom());
            dateField.setValue(event.getDate().toLocalDate());
            formContainer.setVisible(true);
            clearFields();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'affichage du formulaire: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void ajouterReservation() {
        if (validateFields()) {
            try {
                Reservation reservation = new Reservation(
                    0,
                    nomField.getText(),
                    prenomField.getText(),
                    Date.valueOf(dateField.getValue()),
                    selectedEvent.getId()
                );
                
                reservationService.ajouter(reservation);
                showAlert("Succès", "Réservation effectuée avec succès!", Alert.AlertType.INFORMATION);
                clearFields();
                formContainer.setVisible(false);
                
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la réservation: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void annulerReservation() {
        clearFields();
        formContainer.setVisible(false);
    }

    private void clearFields() {
        nomField.clear();
        prenomField.clear();
    }

    private boolean validateFields() {
        if (nomField.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez entrer votre nom", Alert.AlertType.ERROR);
            return false;
        }
        if (prenomField.getText().isEmpty()) {
            showAlert("Erreur", "Veuillez entrer votre prénom", Alert.AlertType.ERROR);
            return false;
        }
        if (dateField.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner une date", Alert.AlertType.ERROR);
            return false;
        }
        if (dateField.getValue().isBefore(LocalDate.now())) {
            showAlert("Erreur", "La date de réservation ne peut pas être dans le passé", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private String truncateText(String text, int limit) {
        if (text == null || text.length() <= limit) {
            return text;
        }
        return text.substring(0, limit) + "...";
    }

    @FXML
    private void retourMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MenuPrincipal.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) cardsContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
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
