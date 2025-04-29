package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.models.Devis;
import org.example.services.ExportExcel;
import org.example.services.Service_devis;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherDemandeDevis {

    @FXML private TableColumn<Devis, Integer> idColumn;
    @FXML private TableColumn<Devis, Integer> quantiteColumn;
    @FXML private TableColumn<Devis, String> propositionColumn;
    @FXML private TableColumn<Devis, Integer> clientColumn;
    @FXML private TableView<Devis> devisTable;
    @FXML private ComboBox<Integer> itemsPerPage;
    @FXML private TextField searchField;
    @FXML private ImageView logo;

    private Devis devis;
    private Parent root;
    private Stage stage;
    private Scene scene;

    @FXML
    void initialize() {
        try {
            // Configuration des colonnes
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            clientColumn.setCellValueFactory(new PropertyValueFactory<>("fermier_id"));
            propositionColumn.setCellValueFactory(new PropertyValueFactory<>("proposition"));
            quantiteColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));

            // Configuration du clic sur une ligne
            setupTableRowClickHandler();

            // Rafraîchissement des données
            refreshTable();
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupTableRowClickHandler() {
        devisTable.setRowFactory(tv -> {
            TableRow<Devis> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    Devis selectedDevis = row.getItem();
                    openReponseDevisWindow(selectedDevis);
                }
            });
            return row;
        });
    }

    private void openReponseDevisWindow(Devis devis) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reponse_devis.fxml"));
            Parent root = loader.load();

            Reponse_devis_service controller = loader.getController();
            controller.initData(devis);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Réponse au Devis #" + devis.getId());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de réponse", Alert.AlertType.ERROR);
        }
    }

    public void refreshTable() {
        try {
            Service_devis service = new Service_devis();
            List<Devis> list = service.afficher();
            ObservableList<Devis> observableList = FXCollections.observableList(list);
            devisTable.setItems(observableList);
        } catch (Exception e) {
            showAlert("Erreur de chargement", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRepondreDevis(ActionEvent event) {
        Devis devisSelectionne = devisTable.getSelectionModel().getSelectedItem();

        if (devisSelectionne == null) {
            showAlert("Aucun devis sélectionné", "Veuillez sélectionner un devis à répondre", Alert.AlertType.WARNING);
            return;
        }
        openReponseDevisWindow(devisSelectionne);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadScene(ActionEvent event, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initData(Devis devis) {
        this.devis = devis;
    }

    @FXML
    public void navDashboard(ActionEvent event) throws IOException {
        loadScene(event, "/Afficher.fxml");
    }

    @FXML
    public void navDevisList(ActionEvent event) throws IOException {
        //loadScene(event, "/afficher_demande_devis.fxml");
    }

    @FXML
    public void navClients(ActionEvent event) throws IOException {
        loadScene(event, "/front.fxml");
    }

    @FXML
    public void navEquipements(ActionEvent event) {
        // Implémentation de navigation
    }

    public void btn_filtre(ActionEvent event) {
        // Implémentation du filtre
    }

    public void recherche(KeyEvent keyEvent) {
        // Implémentation de la recherche
    }

    @FXML
    public void btn_xl(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier Excel");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));
        File file = fileChooser.showSaveDialog(devisTable.getScene().getWindow());

        if (file != null) {
            ExportExcel.ExcelExporter.exportToExcel(devisTable, file.getAbsolutePath());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export réussi");
            alert.setHeaderText(null);
            alert.setContentText("Données exportées vers Excel avec succès !");
            alert.showAndWait();
        }
    }

    public void nav_stats(ActionEvent event) throws IOException {
        loadScene(event,"/statestiques.fxml");
    }
}