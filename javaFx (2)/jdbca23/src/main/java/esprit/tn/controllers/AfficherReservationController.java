package esprit.tn.controllers;

import esprit.tn.entities.Reservation;
import esprit.tn.main.DatabaseConnection;
import esprit.tn.services.ReservationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class AfficherReservationController {

    @FXML
    private TableView<Reservation> reservationTable;

    @FXML
    private TableColumn<Reservation, String> nomColumn;

    @FXML
    private TableColumn<Reservation, String> prenomColumn;

    @FXML
    private TableColumn<Reservation, Date> dateColumn;

    @FXML
    private TableColumn<Reservation, Void> actionsColumn;

    private final ReservationService reservationService;

    public AfficherReservationController() {
        this.reservationService = new ReservationService(DatabaseConnection.getInstance().getCnx());
    }

    @FXML
    public void initialize() {
        setupColumns();
        loadReservations();
    }

    private void setupColumns() {
        nomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        prenomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));
        dateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDate()));

        // Configuration de la colonne d'actions
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                editButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                editButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    modifierReservation(reservation);
                });

                deleteButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    supprimerReservation(reservation);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
    }

    private void loadReservations() {
        reservationTable.setItems(FXCollections.observableArrayList(reservationService.getall()));
    }

    @FXML
    private void ajouterReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReservation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) reservationTable.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur de navigation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void modifierReservation(Reservation reservation) {
        try {
            Dialog<Reservation> dialog = new Dialog<>();
            dialog.setTitle("Modifier Réservation");

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            TextField nomField = new TextField(reservation.getNom());
            TextField prenomField = new TextField(reservation.getPrenom());
            DatePicker datePicker = new DatePicker(reservation.getDate().toLocalDate());

            grid.add(new Label("Nom:"), 0, 0);
            grid.add(nomField, 1, 0);
            grid.add(new Label("Prénom:"), 0, 1);
            grid.add(prenomField, 1, 1);
            grid.add(new Label("Date:"), 0, 2);
            grid.add(datePicker, 1, 2);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    reservation.setNom(nomField.getText());
                    reservation.setPrenom(prenomField.getText());
                    reservation.setDate(Date.valueOf(datePicker.getValue()));
                    return reservation;
                }
                return null;
            });

            dialog.showAndWait().ifPresent(result -> {
                try {
                    reservationService.modifier(result);
                    loadReservations();
                    showAlert("Succès", "Réservation modifiée avec succès!", Alert.AlertType.INFORMATION);
                } catch (SQLException e) {
                    showAlert("Erreur", "Erreur lors de la modification: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            });

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la modification: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void supprimerReservation(Reservation reservation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette réservation ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    reservationService.supprimer(reservation.getId());
                    loadReservations();
                    showAlert("Succès", "Réservation supprimée avec succès!", Alert.AlertType.INFORMATION);
                } catch (SQLException e) {
                    showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    private void retourMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MenuPrincipal.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) reservationTable.getScene().getWindow();
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
