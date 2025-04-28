package esprit.tn.controllers;

import esprit.tn.entities.Event;
import esprit.tn.entities.Reservation;
import esprit.tn.main.DatabaseConnection;
import esprit.tn.services.EventService;
import esprit.tn.services.ReservationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Cursor;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AfficherEventController {

    @FXML
    private FlowPane cardsContainer;

    @FXML
    private LineChart<String, Number> chartPrediction;

    private final EventService eventService;
    private final ReservationService reservationService;

    public AfficherEventController() {
        this.eventService = new EventService(DatabaseConnection.getInstance().getCnx());
        this.reservationService = new ReservationService(DatabaseConnection.getInstance().getCnx());
    }

    @FXML
    public void initialize() {
        loadEventCards();
        updatePredictionChart();
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

        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetY(3.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setColor(Color.color(0, 0, 0, 0.2));
        card.setEffect(dropShadow);

        card.setOnMouseEntered(e -> {
            dropShadow.setOffsetY(6.0);
            dropShadow.setOffsetX(6.0);
            dropShadow.setColor(Color.color(0, 0, 0, 0.3));
            card.setCursor(Cursor.HAND);
        });

        card.setOnMouseExited(e -> {
            dropShadow.setOffsetY(3.0);
            dropShadow.setOffsetX(3.0);
            dropShadow.setColor(Color.color(0, 0, 0, 0.2));
            card.setCursor(Cursor.DEFAULT);
        });

        Label nomLabel = new Label(event.getNom());
        nomLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label descLabel = new Label(event.getDescr());
        descLabel.setWrapText(true);

        Label dateLabel = new Label("Date: " + event.getDate().toString());
        Label typeLabel = new Label("Type: " + event.getType());

        // Création des boutons
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);

        Button modifierBtn = new Button("Modifier");
        modifierBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        modifierBtn.setOnAction(e -> modifierEvent(event));

        Button supprimerBtn = new Button("Supprimer");
        supprimerBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        supprimerBtn.setOnAction(e -> supprimerEvent(event));

        buttonsBox.getChildren().addAll(modifierBtn, supprimerBtn);

        card.getChildren().addAll(nomLabel, descLabel, dateLabel, typeLabel, buttonsBox);

        return card;
    }

    private void modifierEvent(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierEvent.fxml"));
            Parent root = loader.load();
            
            ModifierEventController controller = loader.getController();
            controller.initData(event);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier l'événement");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // Rafraîchir l'affichage après modification
            loadEventCards();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture du formulaire de modification: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void supprimerEvent(Event event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'événement");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer l'événement \"" + event.getNom() + "\" ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                eventService.supprimer(event.getId());
                loadEventCards();
                showAlert("Succès", "L'événement a été supprimé avec succès.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la suppression de l'événement: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
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
    private void allerVersVueClient() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/affichageeventfront.fxml"));
            Stage stage = (Stage) cardsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du chargement de la vue client: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void refreshTables() {
        loadEventCards();
    }

    @FXML
    private void goToCalendrier(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Calendrier.fxml"));
            Parent root = loader.load();
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.show();
        } catch (NullPointerException e) {
            showError("Fichier introuvable", "Le fichier Calendrier.fxml n'a pas été trouvé à l'emplacement spécifié.");
        } catch (IOException e) {
            showError("Erreur de chargement", "Une erreur est survenue lors du chargement de l'interface: " + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void updatePredictionChart() {
        if (chartPrediction == null) return;
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Prédiction réservations");
        var events = eventService.getall();
        var reservations = reservationService.getall();
        for (Event event : events) {
            int prediction = esprit.tn.utils.EventRecommender.predictReservations(event, events, reservations);
            series.getData().add(new XYChart.Data<>(event.getNom(), prediction));
        }
        chartPrediction.getData().clear();
        chartPrediction.getData().add(series);
    }
}