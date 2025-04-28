package esprit.tn.controllers;

import esprit.tn.entities.Event;
import esprit.tn.entities.Reservation;
import esprit.tn.main.DatabaseConnection;
import esprit.tn.services.EventService;
import esprit.tn.services.ReservationService;
import esprit.tn.utils.EventRecommender;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AfficherEventfrontController {

    @FXML private FlowPane cardsContainer;
    @FXML private VBox formContainer;
    @FXML private Label selectedEventLabel;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private DatePicker dateField;
    @FXML private ImageView qrImageView;
    @FXML private Label labelMostRecommended;
    @FXML private Label labelPrediction;
    @FXML private LineChart<String, Number> chartPrediction;

    private final EventService eventService;
    private final ReservationService reservationService;
    private Event selectedEvent;

    public AfficherEventfrontController() {
        this.eventService = new EventService(DatabaseConnection.getInstance().getCnx());
        this.reservationService = new ReservationService(DatabaseConnection.getInstance().getCnx());
    }

    @FXML
    public void initialize() {
        loadEventCards();
        formContainer.setVisible(false);
        qrImageView.setVisible(false);
        updatePredictionChart();
    }

    private void loadEventCards() {
        cardsContainer.getChildren().clear();
        for (Event event : eventService.getall()) {
            VBox card = createEventCard(event);
            cardsContainer.getChildren().add(card);
        }
    }

    private VBox createEventCard(Event event) {
        VBox card = new VBox();
        card.setSpacing(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        card.setPrefWidth(300);
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 0, 0);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);"));

        ImageView imageView = new ImageView();
        imageView.setFitWidth(260);
        imageView.setFitHeight(150);
        imageView.setStyle("-fx-background-radius: 10;");

        if (event.getPhoto() != null && !event.getPhoto().isEmpty()) {
            try {
                File file = new File(event.getPhoto());
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    imageView.setImage(image);
                    imageView.setPreserveRatio(true);
                }
            } catch (Exception e) {
                System.err.println("Erreur de chargement de l'image: " + e.getMessage());
            }
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

        VBox detailsBox = new VBox(5);
        detailsBox.getChildren().addAll(typeBox, dateBox);

        Button reserverBtn = new Button("Réserver");
        reserverBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        reserverBtn.setOnAction(e -> afficherFormulaireReservation(event));

        card.getChildren().addAll(imageView, titleLabel, descLabel, detailsBox, reserverBtn);

        return card;
    }

    private String truncateText(String text, int limit) {
        if (text == null || text.length() <= limit) {
            return text;
        }
        return text.substring(0, limit) + "...";
    }

    private void afficherFormulaireReservation(Event event) {
        this.selectedEvent = event;
        selectedEventLabel.setText("Réservation pour: " + event.getNom());
        dateField.setValue(event.getDate().toLocalDate());
        formContainer.setVisible(true);
    }

    @FXML
    private void ajouterReservation() {
        if (!validateInputs()) {
            return;
        }

        try {
            Reservation reservation = new Reservation(
                    nomField.getText().trim(),
                    prenomField.getText().trim(),
                    Date.valueOf(dateField.getValue()),
                    selectedEvent.getId()
            );

            reservationService.ajouter(reservation);
            showAlert("Succès", "Réservation effectuée avec succès", Alert.AlertType.INFORMATION);

            // QR code encode un message personnalisé avec nom/prénom
            String nom = nomField.getText();
            String prenom = prenomField.getText();
            String eventName = selectedEvent.getNom();
            String message = "Bienvenue à l'événement '" + eventName + "'\n" + nom + " " + prenom + ", votre réservation est confirmée !";
            String qrPath = "qr_" + nom + "_" + prenom + ".png";
            esprit.tn.utils.QRAndPDFUtil.generateQRCode(message, qrPath, 300, 300);
            showQRCode(qrPath);
            resetFormExceptQR();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la réservation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showQRCode(String qrPath) {
        File qrFile = new File(qrPath);
        if (qrFile.exists()) {
            Image qrImage = new Image(qrFile.toURI().toString());
            qrImageView.setImage(qrImage);
            qrImageView.setVisible(true);
        } else {
            qrImageView.setVisible(false);
        }
    }

    private void resetFormExceptQR() {
        nomField.clear();
        prenomField.clear();
        dateField.setValue(null);
    }

    @FXML
    private void annulerReservation() {
        resetForm();
    }

    private void resetForm() {
        selectedEvent = null;
        nomField.clear();
        prenomField.clear();
        dateField.setValue(null);
        selectedEventLabel.setText("");
        formContainer.setVisible(false);
        qrImageView.setVisible(false);
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();

        if (selectedEvent == null) {
            errors.append("Aucun événement sélectionné.\n");
        }
        if (nomField.getText().trim().isEmpty()) {
            errors.append("Le nom est requis.\n");
        }
        if (prenomField.getText().trim().isEmpty()) {
            errors.append("Le prénom est requis.\n");
        }
        if (dateField.getValue() == null) {
            errors.append("La date est requise.\n");
        } else if (dateField.getValue().isBefore(LocalDate.now())) {
            errors.append("La date ne peut pas être dans le passé.\n");
        }

        if (errors.length() > 0) {
            showAlert("Erreur de validation", errors.toString(), Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    @FXML
    private void showReservationRecommendation() {
        // Récupère toutes les réservations depuis le service
        List<Reservation> reservations = reservationService.getall();
        Map<Integer, Long> eventCount = reservations.stream()
                .collect(Collectors.groupingBy(Reservation::getEventId, Collectors.counting()));
        if (eventCount.isEmpty()) {
            labelMostRecommended.setText("Aucun événement recommandé.");
            cardsContainer.getChildren().clear();
            return;
        }
        // Trouve l'eventId le plus réservé
        int mostReservedId = eventCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get().getKey();
        long count = eventCount.get(mostReservedId);
        // Récupère le nom de l'événement
        List<Event> allEvents = eventService.getall();
        Event mostRecommendedEvent = allEvents.stream()
                .filter(e -> e.getId() == mostReservedId)
                .findFirst()
                .orElse(null);
        if (mostRecommendedEvent == null) {
            labelMostRecommended.setText("Aucun événement recommandé.");
            cardsContainer.getChildren().clear();
            return;
        }
        labelMostRecommended.setText("Événement le plus recommandé : " + mostRecommendedEvent.getNom() + " (" + count + " réservations)");
        // Affiche uniquement la carte de l'événement le plus recommandé
        cardsContainer.getChildren().clear();
        VBox card = createEventCard(mostRecommendedEvent);
        cardsContainer.getChildren().add(card);
    }

    @FXML
    private void retourMenu() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/MenuPrincipal.fxml"));
            Stage stage = (Stage) cardsContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du retour au menu: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void allerVersCalendrier() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Calendrier.fxml"));
            Stage stage = (Stage) cardsContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture du calendrier: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void predictOnAddEvent(Event event) {
        int prediction = esprit.tn.utils.EventRecommender.predictReservations(
            event,
            eventService.getall(),
            reservationService.getall()
        );
        labelPrediction.setText("Prédiction : " + prediction + " réservations attendues");
    }

    private void updatePredictionChart() {
        if (chartPrediction == null) return;
        chartPrediction.getData().clear();
        List<Event> events = eventService.getall();
        List<Reservation> reservations = reservationService.getall();
        // Pour chaque événement, créer une série (courbe) distincte, X = nom de l'événement, Y = nombre de réservations
        for (Event event : events) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(event.getNom());
            int count = (int) reservations.stream().filter(r -> r.getEventId() == event.getId()).count();
            // X = nom de l'événement, Y = nombre de réservations
            series.getData().add(new XYChart.Data<>(event.getNom(), count));
            chartPrediction.getData().add(series);
        }
    }
}