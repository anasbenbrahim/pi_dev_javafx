package org.example.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.models.Equipements;
import org.example.services.Service_equipement;

import java.io.IOException;
import java.util.List;

public class Affichage {



    @FXML private Button btn_modifier;
    @FXML private Button ajout;
    @FXML private TableColumn<Equipements, String> description;
    @FXML private TableColumn<Equipements, String> category;
    @FXML private TableColumn<Equipements, String> image;
    @FXML private TableColumn<Equipements, String> nom;
    @FXML private TableColumn<Equipements, Double> prix;
    @FXML private TableColumn<Equipements, Integer> quantite;
    @FXML private TableView<Equipements> view;
    @FXML private Button supprimer;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    void initialize() {
        try {
            refreshTable();

            // Set up cell value factories
            nom.setCellValueFactory(new PropertyValueFactory<>("nom"));
            category.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getNomCategorie()));
            prix.setCellValueFactory(new PropertyValueFactory<>("prix"));
            quantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
            description.setCellValueFactory(new PropertyValueFactory<>("description"));
            image.setCellValueFactory(new PropertyValueFactory<>("image"));

            // Custom cell factory for image column
            image.setCellFactory(column -> new TableCell<Equipements, String>() {
                private final ImageView imageView = new ImageView();

                @Override
                protected void updateItem(String imagePath, boolean empty) {
                    super.updateItem(imagePath, empty);

                    if (empty || imagePath == null || imagePath.isEmpty()) {
                        setGraphic(null);
                    } else {
                        try {
                            Image img;
                            if (imagePath.startsWith("http") || imagePath.startsWith("https")) {
                                img = new Image(imagePath,true);
                            } else if (imagePath.startsWith("C:")) {
                                img = new Image("file:" + imagePath);
                            } else {
                                img = new Image(getClass().getResourceAsStream("/images/" + imagePath));
                            }
                            imageView.setImage(img);
                            imageView.setFitWidth(150);
                            imageView.setFitHeight(150);
                            imageView.setPreserveRatio(true);
                            setGraphic(imageView);
                        } catch (Exception e) {
                            setText("Image not found");
                            setGraphic(null);
                        }
                    }
                }
            });

            // Set up row click handler (double click to modify)
            view.setRowFactory(tv -> {
                TableRow<Equipements> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && !row.isEmpty()) {
                        Equipements selectedEquipment = row.getItem();
                        openModificationWindow(selectedEquipment);
                    }
                });
                return row;
            });

        } catch (Exception e) {
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void refreshTable() {
        try {
            Service_equipement service = new Service_equipement();
            List<Equipements> list = service.afficher();
            ObservableList<Equipements> observableList = FXCollections.observableList(list);
            view.setItems(observableList);
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void openModificationWindow(Equipements equipement) {
        try {
            System.out.println("Opening Modifier for: " + equipement); // Debug log
            if (equipement == null) {
                showAlert("Erreur", "Aucun équipement sélectionné", Alert.AlertType.ERROR);
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Modifier.fxml"));
            Parent root = loader.load();

            Modifier controller = loader.getController();
            controller.initData(equipement, this::refreshTable); // Pass refresh callback

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier équipement");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Log the full error
            showAlert("Erreur", "Erreur lors de l'ouverture de la fenêtre: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void nav_supprimer(ActionEvent event) throws IOException {
        loadScene(event, "/Supprimer.fxml");
    }

    @FXML
    void nav_ajouter(ActionEvent event) throws IOException {
        loadScene(event, "/Ajout.fxml");
    }

    @FXML
    void nav_modifier(ActionEvent event) throws IOException {
        loadScene(event, "/Modifier.fxml");
    }

    private void loadScene(ActionEvent event, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}